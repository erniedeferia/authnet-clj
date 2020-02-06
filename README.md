# authnet-clj - A native Clojure Authorize.Net client

Authorize.Net Clojure - AuthNet-Clj - is a native Clojure client for the Authorize.Net JSON API. It aims to cover mostly the ARB, CIM, and Transactions end-points.


## Usage

```clojure
[authnet-clj "0.1.0"]

(ns my.app.namespace
  (:require [authnet-clj :refer :all]))


;; example 1: to get suspended subscriptions

(get-suspended-subscriptions)

;; exmaple 2: to update payment method (credit card) on existing subscription

(update-subscription "123456" "370000000000002" "2020-12")

```

## Configuration

AuthNet-Clj expects the following Authorize.Net credentials as environment variables.

```sh
AUTHORIZENET_GATEWAY=sandbox
AUTHORIZENET_LOGIN_ID
AUTHORIZENET_TRANSATION_KEY
```

When `AUTHORIZENET_GATEWAY` is set to sandbox, `AutNet-Clj` will operate on the test (or sandbox)
environment. A sandbox account can be created at the following location:


https://developer.authorize.net/hello_world/sandbox/



# Authorize.Net API Documentation & Reference

https://developer.authorize.net/api/reference
https://developer.authorize.net/api/reference/index.html#recurring-billing-update-a-subscription


Copyright © 2020 MACROFEX LLC

