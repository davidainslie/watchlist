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

## Customer deletes item from their watchlist

Let's add our first item and then delete it:

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

```bash
$ http DELETE localhost:8080/watchlist/321/12345
HTTP/1.1 200 OK
Content-Length: 31
Content-Type: application/json
Date: Tue, 04 Dec 2018 00:43:32 GMT

{
    "customerId": "321",
    "items": []
}
```

