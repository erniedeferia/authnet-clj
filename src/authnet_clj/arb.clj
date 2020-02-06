;; ## ARB - Automatic Recurring Billing
;;
;; This namespace implements the functions require to list, get, create, and update
;; recurring billing subscriptions.
;;
(ns authnet-clj.arb
  (:require [authnet-clj.core :refer :all] )
  )


;; # Records to facilitate subscription update.
;;
;; The following example illustrates the basic structure for
;; a subscription payment update. The records defined below
;; reflect each of the segments of this structure.
;;
;; <pre>
;; <code>
;; {
;;     "ARBUpdateSubscriptionRequest": {
;;         "merchantAuthentication": {
;;             "name": "********",
;;             "transactionKey": "*******************"
;;         },
;;         "refId": "123456",
;;         "subscriptionId": "100748",
;;         "subscription": {
;;             "payment": {
;;                 "creditCard": {
;;                     "cardNumber": "4111111111111111",
;;                     "expirationDate": "2020-12"
;;                 }
;;             }
;;         }
;;     }
;; }
;; </code>
;; </pre>


;; The billing address associated with a credit card update
(defrecord billTo [firstName lastName address city state zip])

;; Credit card record with card number and expiration date attributes
(defrecord creditCard [cardNumber expirationDate])

;; A payment segnment includes a credit card map
(defrecord payment [^creditCard creditCard])

;; A subscription update can include a new CC as well as
;; a new billTo (name, address). The `update-subscription`
;; function has two arities to support these two scenarios.
(defrecord subscription [^payment payment ^billTo billTo])


(defn make-subscription-payment-update
  " Makes the map required to submit a subscription payment method update request.
  "
 ([credit-card expiration-date]
  (->subscription (->payment (->creditCard credit-card expiration-date))))
 ([credit-card expiration-date f-name l-name address city state zip]
  (->subscription (->payment (->creditCard credit-card expiration-date)) (->billTo f-name l-name address city state zip)))
  )


;;
;; ## Concrete Subscription Request Functions
;; <br/>

(defn get-subscription-status
  "Retrieves the subscription status for the specified subscription.
  "
  [subscription-id]
  (let [creds (get-credentials)
        url (get-endpoint creds)]
    (do-request url (build-request :ARBGetSubscriptionStatusRequest {:subscriptionId subscription-id} ) )
    )
  )

(defn get-subscription
  "Retrieves the details of the specified subscription.
  "
  [subscription-id]
  (let [creds (get-credentials)
        url (get-endpoint creds)]
    (do-request url (build-request :ARBGetSubscriptionRequest {:subscriptionId subscription-id} ) )
    )
  )


(defn get-subscriptions
  " Retrieves a sequence of subscriptions in the specified status.

  Inputs:
     <pre>
     search-type: cardExpiringThisMonth
                  subscriptionActive
                  subscriptionInactive
                  subscriptionExpiringThisMonth
     </pre>
  "
  [search-type]
  (let [creds (get-credentials)
        url (get-endpoint creds)]
    (do-request url (build-request :ARBGetSubscriptionListRequest {:searchType search-type }))
    )
  )


(defn update-subscription
  " Updates the Credit Card number and Expirate Date of the specified subscription.
  It can also update the Credit Card holder's name and address if 9-arity function
  is invoked.
  "
  ([subscription-id cc-number cc-expiration]
   (let [creds (get-credentials)
         url (get-endpoint creds)
         ]
     (do-request url (build-request :ARBUpdateSubscriptionRequest
                                    {:subscriptionId subscription-id
                                     :subscription (make-subscription-payment-update cc-number cc-expiration )}))))
  ([subscription-id cc-number cc-expiration fname lname address city state zip]
   (let [creds (get-credentials)
         url (get-endpoint creds)
         ]
     (do-request url (build-request :ARBUpdateSubscriptionRequest
                                    {:subscriptionId subscription-id
                                     :subscription (make-subscription-payment-update cc-number cc-expiration fname lname address city state zip)})))
   ))

(defn cancel-subscription
  "Makes a request to cancel the identified subscription.
  "
  [subscription-id]
  (let [creds (get-credentials)
        url (get-endpoint creds)
        ]
    (do-request url  (build-request :ARBCancelSubscriptionRequest {:subscriptionId subscription-id}))
    )
  )
