 <html>
    <body>
        <%# We show the cookie later because it's way to long %>
        <h3>Incoming headers (without cookie):</h3>
        <table>
            <tr>
                <th>Key</th>
                <th>Value</th>
            </tr>
            %%for [key, value] in request.headers.zip():
                %%if key != "cookie":
                    <tr>
                        <td><%=key%></td>
                        <td><%=value%></td>
                    </tr>
                %%end
            %%end
        </table>

        <h5>Cookie is: <%=request.headers.cookie%></h5>
    <body>
</html>