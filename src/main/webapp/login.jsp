<%@ page pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <title>Login</title>
    <link type="text/css" rel="stylesheet" href="form.css" />
</head>
<body>
    <form method="post" action="login">
        <fieldset>
            <legend>Login</legend>
            <label for="email">Email address</label>
            <input type="text" id="email" name="email" size="20" maxlength="60" />
            <br />

            <label for="password">Password</label>
            <input type="password" id="password" name="password" size="20" maxlength="20" />
            <br />

            <input type="submit" value="Connect" class="sansLabel" />
            <br />
            <p><a href="register.jsp">Don't have an account? Sign up here.</a></p>
        </fieldset>
    </form>
</body>
</html>