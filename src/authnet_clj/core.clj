;; ## Core Common functions
;;
;; This namespace holds core functions used by each of the (ARB, CIM, Trans)
;; request-specific namespaces.
;;
(ns authnet-clj.core
  (:require [cheshire.core :refer :all ]
            [clj-http.client :as client]
            )
  (:gen-class))


(def endpoints
  "Defines the sandbox and production end-points"
  { :sandbox "https://apitest.authorize.net/xml/v1/request.api"
    :production "https://api.authorize.net/xml/v1/request.api" })

(defn get-credentials
  "Retrieves the Authorize.net credentials from environment variables."
  []
  (let [name (System/getenv "AUTHORIZENET_LOGIN_ID")
        key (System/getenv "AUTHORIZENET_TRANSACTION_KEY")
        gateway (System/getenv "AUTHORIZENET_GATEWAY")
        ]

    (if (nil? name)
      (throw (ex-info "Environment variable AUTHORIZENET_LOGIN_ID is missing" {}))
      )

    (if (nil? key)
      (throw (ex-info "Environment variable AUTHORIZENET_TRANSACTION_KEY is missing" {}))
      )

    (if (nil? gateway)
      (throw (ex-info "Environment variable AUTHORIZENET_GATEWAY is missing" {}))
      )

  {:name name
   :transactionKey key
   :gateway gateway } )
)

;; Helper functions to get each Authorize.net credential variable.
(defn get-name [creds] (:name (get-credentials)))
(defn get-transaction-key [creds] (:transactionKey (get-credentials)))
(defn get-gateway [creds] (keyword (:gateway (get-credentials)) )  )
(defn get-endpoint [creds]
  ((get-gateway creds) endpoints))


(defn get-marchant-authentication-map
  "Common transaction request maps generators. These are used
  within each of the API segements (ARB, Customer Profile, etc.)
  "
  [creds]
  {:merchantAuthentication
   {:name (:name creds) :transactionKey (:transactionKey creds)}}
  )

(defn get-transaction-map
  "Generates the complete transaction request map including the transaction name along
  with the merchant Authentication segment followed by the transaction payload.

  Inputs:
  -------
  transaction-name: a keyword, such as :ARBGetSubscriptionstatusrequest that identifies
                    the transactions to be submitted.

  merchantauth-map: a map with a single key :merchantAuthentication and which value
                    is a map containing the API Login ID and API Transaction Key.

  Note: Use the get-merchant-authentication-map helper to generate the merchantauth-map.

  payload-map:      a map of k/v that is specific to the transaction (transaction-name).
                    (https://developer.authorize.net/api/reference)

  Output:
  -------
  A map similar to the one depicted below, but with the k/v that are specific
  to a transaction after the merchantAuthentication map.

  <pre>
  <code>
  {
    'ARBGetSubscriptionStatusRequest': {
        'merchantAuthentication': {
            'name': '<AUTHORIZINET_LOGIN_ID>',
            'transactionKey': '<AUTHORIZENET_TRANSACTION_KEY>'
        },
        'refId': '123456',
        'subscriptionId': '100748'
    }
  }
  </code>
  </pre>

  "
  [merchantauth-map transaction-name payload-map]
  {transaction-name  (merge  merchantauth-map payload-map) }
  )

(defn build-request
  "Request builder responsible for creating a properly formed
  request map including the merchant Authentication segment.
  "
  [request-type request-payload]
  (-> (get-credentials)
      (get-marchant-authentication-map)
      (get-transaction-map request-type request-payload )
      )
  )

(defn do-request
 "Request producer responsible for issuing the POST request to the Authorize.Net end-point
  specified via the environment variables.

  Note: Authorize.Net returns BOM (Byte Order Marker) byte that screws
  up the JSON parser. The combination of reading the body as
  a stream and removing the BOM \uFEFF does the trick.
  "
  [url query]
  (let [res (client/post url
               {:body (generate-string query )
                :accept :json
                :body-encoding "UTF-16"
                :as :stream
                :throw-exceptions? false
                :content-type :json}) ]
    (-> res
        (:body)
        (slurp)
        (clojure.string/replace #"\r\n|\n|\r|\uFEFF" "")
        (parse-string true)
        )
    )
  )
