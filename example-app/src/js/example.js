import { ForgeRockAuth } from 'forgerock-auth';

window.testEcho = () => {
    const inputValue = document.getElementById("echoInput").value;
    ForgeRockAuth.echo({ value: inputValue })
}
