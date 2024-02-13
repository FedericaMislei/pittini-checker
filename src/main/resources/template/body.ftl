
<table style="width: 100%; border: 1px solid gray;border-spacing: 1px; background-color: lightgray;">
    <tr>
        <th style="background-color: white;">Controllo</th>
        <th style="background-color: white;">valori</th>
        <th style="background-color: white;">Istruzioni</th>

    </tr>
    <#list list as r>
        <#if email == "true">
            <#if r.getErrore()>
            <tr style="color:red">
            <#else>
            <tr>
            </#if>
                <td style="background-color: white;">${r.getControllo1()}</td>
                <td style="background-color: white;">${r.getValue1()} <#if r.getValue2()??> - ${r.getValue2()}</#if></td>
                <td style="background-color: white;">${r.getIstruzioni1()}</td>
            </tr>
        <#else>
                <tr>
                    <td style="background-color: white;">${r.getControllo1()}</td>
                    <td style="background-color: white;">${r.getValue1()} <#if r.getValue2()??> - ${r.getValue2()}</#if></td>
                    <td style="background-color: white;">${r.getIstruzioni1()}</td>
                </tr>
        </#if>
    </#list>
</table>
