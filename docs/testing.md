# Testing

## Unit

```bash
$ sbt test
```

## Integration

Currently not required

## Acceptance

Since Docker has not be set up yet, before running the acceptance tests, the application will have to be booted first:

```bash
$ sbt run
```

then:

```bash
$ sbt acceptance:test
```