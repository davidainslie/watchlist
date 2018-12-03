# API

Assuming you installed **httpie** as per the [Setup](setup.md):

## Customer adds item to their watchlist

```bash
$ http POST localhost:8080/watchlist/321 contentId=12345
HTTP/1.1 201 Created
Content-Length: 52
Content-Type: application/json
Date: Mon, 03 Dec 2018 22:58:56 GMT

{
    "customerId": "321",
    "items": [
        {
            "contentId": "12345"
        }
    ]
}
```

and add one more for luck:

```bash
$ http POST localhost:8080/watchlist/321 contentId=56789
HTTP/1.1 201 Created
Content-Length: 74
Content-Type: application/json
Date: Mon, 03 Dec 2018 22:59:55 GMT

{
    "customerId": "321",
    "items": [
        {
            "contentId": "12345"
        },
        {
            "contentId": "56789"
        }
    ]
}
```

## Customer accesses their watchlist

When customer is not set up in the system:

```bash
$ http GET localhost:8080/watchlist/123
HTTP/1.1 404 Not Found
Content-Length: 71
Content-Type: application/json
Date: Mon, 03 Dec 2018 21:09:35 GMT

{
    "error": {
        "message": "Non existing customer provided: CustomerId(123)"
    }
}
```

And an existing customer with items in their watchlist:

```bash
$ http GET localhost:8080/watchlist/321
HTTP/1.1 200 OK
Content-Length: 74
Content-Type: application/json
Date: Mon, 03 Dec 2018 23:01:31 GMT

{
    "customerId": "321",
    "items": [
        {
            "contentId": "12345"
        },
        {
            "contentId": "56789"
        }
    ]
}
```







- 

- Customer can delete contentIDs from their Watchlist 

- 

- Customer cannot see another customer’s Watchlist 

- 

- 

- 

  Examples: 

API. A single watchlist item is represented by a 5 digit 

alphanumeric string (called a contentID) that is unique to a specific asset. The client teams will send 

contentIDs to the new Watchlist service. 

Given a customer with id ‘123’ and an empty Watchlist
 When the customer adds ContentIDs ‘zRE49’, ‘wYqiZ’, ‘15nW5’, ‘srT5k’, ‘FBSxr’ to their watchlist Then their Watchlist is returned it should only include ContentIDs ‘zRE49’, ‘wYqiZ’, ‘15nW5’, ‘srT5k’, ‘FBSxr’ 

Given a customer with id ‘123’ and a Watchlist containing ContentIDs ‘zRE49’, ‘wYqiZ’, ‘15nW5’, ‘srT5k’, ‘FBSxr’
 When they remove ContentID ‘15nW5’ from their Watchlist
 Then their Watchlist should only contain ContentIDs ‘zRE49’, ‘wYqiZ’, ‘srT5k’, ‘FBSxr’ 

Given two customers, one with id ‘123’ and one with id ‘abc’
 And corresponding Watchlists containing ContentIDs ‘zRE49’, ‘wYqiZ’, ‘srT5k’, ‘FBSxr’ and ‘hWjNK’, ’U8jVg’, ‘GH4pD’, ’rGIha’ respectively
 When customer with id ‘abc’ views their Watchlist they should only see ContentIDs ‘hWjNK’, ’U8jVg’, ‘GH4pD’, ’rGIha’ 