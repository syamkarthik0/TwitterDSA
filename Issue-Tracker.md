JWT --> Complete flow with Diagram. JWT Token Flow, Fix bug and raise PR. (24th Dec)

Frontend (App.js):
Clear local storage FIRST, before making the server request
Remove unnecessary credentials: "include"
Always redirect to login page, regardless of server response
Better error handling
Backend (AuthController):
Always return 200 OK for logout requests
Handle expired tokens gracefully
Better error handling and logging
Try to logout the user even if token is expired
The main philosophy behind these changes is:

Logout should ALWAYS succeed from the user's perspective
Clear client-side state (localStorage) first
Then attempt to clear server-side state
Always redirect to login page


Normal logout
Expired token
Network error
Server error

###########################################################

Feed Generation -- 25th.(wednesday) ---> 
Graph Explanation -- 26th.(Thursday)
Cache and BST explanation -- 27th.(Friday) how to use cache (example for users, tweets, comments)
Search Functionality in Frontend -- Search for users.