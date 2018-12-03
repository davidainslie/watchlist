# API

Assuming you installed **httpie** as per the [Setup](setup.md):

## Customer adds "item" to their watchlist

```bash
$ http POST localhost:8080/node node-ip=localhost:5002
HTTP/1.0 201 CREATED
...
{
    "data": {
        "message": "Node added successfully",
        "nodes": [
            "localhost:5001",
            "localhost:5002"
        ]
    }
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

Customer with one item in their watchlist:

```bash
$ http GET localhost:8080/watchlist/123
```







- Customer can add contentIDs to their Watchlist 

- Customer can delete contentIDs from their Watchlist 

- Customer can see contents they added in their Watchlist 

- Customer cannot see another customer’s Watchlist 

- Each customer is represented by a unique 3 digit alphanumeric string 

- The Watchlist items should be stored in memory 

- The API should produce and consume JSON 

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