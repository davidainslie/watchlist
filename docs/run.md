# Run

## Standalone

```bash
$ sbt run
```

Then the API can be reached "locally" either from the command line using [curl](https://curl.haxx.se) or [httpie](https://httpie.org) or from your browser.
(Note that not all functionality can be used in the browser as at the time of writing there is no actual UI).

E.g. if you installed **httpie** as per the [Setup](setup.md):

```bash
$ http localhost:8080/healthz
  HTTP/1.1 200 OK
  Content-Length: 19
  Content-Type: application/json
  Date: Sun, 02 Dec 2018 11:19:30 GMT
  
  {
      "data": {
        "message": "Healthy"
      }
  }
```