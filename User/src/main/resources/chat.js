const box = document.getElementById('messages');
const me = Number(box.dataset.me);
const other = Number(box.dataset.other);

const ws = new WebSocket("ws://" + location.hostname + ":8887");
ws.onopen = () => console.log("WebSocket connected");

ws.onmessage = (e) => {
    const m = JSON.parse(e.data);
    const inThisChat = (m.from === me && m.to === other ||
    m.from === other && m.to === me);
    if(!inThisChat) return;

    const p = document.createElement('p');
    const b = document.createElement('b');
    b.textContent = m.name + ': ';
    p.appendChild(b);
    p.appendChild(document.createTextNode(m.content));
    box.appendChild(p);
    p.scrollIntoView();
}

const form = document.getElementById('sendForm');
const input = document.getElementById('content');

form.addEventListener('submit', async(ev) => {
    ev.preventDefault();
    if (input.value.trim() === '') return;
    const body = new URLSearchParams(new FormData(form));
    try {
        const res = await fetch('/send', {
            method: 'POST',
            body: body,
            headers: { 'X-Requested-With' : 'fetch'}
        });
    if (res.ok) { input.value = ''; input.focus(); }
    else { alert('Message not sent'); }
    } catch (err) {
        alert("Network error");
    }
});
if (box.lastElementChild) box.lastElementChild.scrollIntoView();