;; ## Customer Profile
;;
;; This namespace implements the functions related to retrieving and updating
;; customer profiles.
;;
(ns authnet-clj.profiles
  (:require [authnet-clj.core :refer :all])
  )


(defn get-customer-profile
  "Retrieves a customer profile."
  [customer-profile-id]
  (let [creds (get-credentials)
        url (get-endpoint creds)
        ]
    (do-request url (build-request :getCustomerProfileRequest {:customerProfileId customer-profile-id :includeIssuerInfo "true"}))
    )
  )

(defn list-expiring-customers-payment-profiles
  "Lists all payment profiles that expire in the specified month.
     month: \"2020-12\"
     </pre>
  "
  [month]
  (let [creds (get-credentials)
        url (get-endpoint creds)
        ]
    (do-request url (build-request :getCustomerPaymentProfileListRequest {:searchType "cardsExpiringInMonth" :month month}))
    ))

(defn list-customers-profile-ids
  "Lists all customers profiles IDs."
  []
  (let [creds (get-credentials)
        url (get-endpoint creds)
        ]
    (do-request url (build-request :getCustomerProfileIdsRequest {} ))
    )
  )
