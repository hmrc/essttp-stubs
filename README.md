
# essttp-stubs


## Email verification data

### Request passcode

The table below describes responses to the "request email passcode" API. The response is triggered by the email address in
the request. The difference responses described in the table have different HTTP status codes and different error code
strings that form part of the JSON response body in the case of an error.

| email address                      | request passcode response | response error code       | 
| ---------------------------------- | ------------------------- | ------------------------- |
| `no_session_id@email.com`          | 401 (unauthorised)        | `NO_SESSION_ID`           |
| `email_verified_already@email.com` | 409 (conflict)            | `EMAIL_VERIFIED_ALREADY`  |
| `max_emails_exceeded@email.com`    | 403 (forbidden)           | `MAX_EMAILS_EXCEEDED`     |
| `bad_email_request@email.com`      | 400 (bad request)         | `BAD_EMAIL_REQUEST`       |
| `upstream_error@email.com`         | 502 (bad gateway)         | `UPSTREAM_ERROR`          |
| anything else                      | 201 (created)             | -                         |

### Verify passcode

The table below describes responses to the "verify email passcode" API. The response is triggered by the passcode in
the request. The difference responses described in the table have different HTTP status codes and different error code
strings that form part of the JSON response body in the case of an error.

| passcode      | verify passcode response | response error code              |
| ------------- | ------------------------ | -------------------------------- |
| `BBBBBB`      | 401 (unauthorised)       | `NO_SESSION_ID`                  | 
| `CCCCCC`      | 403 (forbidden)          | `MAX_PASSCODE_ATTEMPTS_EXCEEDED` |
| `DDDDDD`      | 404 (not found)          | `PASSCODE_NOT_FOUND`             |
| `FFFFFF`      | 404 (not found)          | `PASSCODE_MISMATCH`              | 
| `GGGGGG`      | 204 (no content)         | -                                |
| anything else | 201 (created)            | -                                |

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").