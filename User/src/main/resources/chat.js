const box = document.getElementById('messages');
const me = box ? Number(box.dataset.me) : null;
const other = box ? Number(box.dataset.other) : null;
let retryDelay = 1000;
const MAX_DELAY = 30000;
let firstConnection = true;
let leaving = false;
let retryScheduled = false;
window.addEventListener('beforeunload', () => { leaving = true; });

const form = document.getElementById('sendForm');
if (form) {
    const input = document.getElementById('content');
    form.addEventListener('submit', async (ev) => {
        ev.preventDefault();
        if (input.value.trim() === '') return;
        const body = new URLSearchParams(new FormData(form));
        try {
            const res = await fetch('/send', {
                method: 'POST',
                body: body,
                headers: {'X-Requested-With': 'fetch'}
            });
            if (res.ok) {
                input.value = '';
                input.focus();
            } else {
                alert('Message not sent');
            }
        } catch (err) {
            alert("Network error");
        }
    });
}
if (box && box.lastElementChild) box.lastElementChild.scrollIntoView();


function handleMessage(e) {
    const m = JSON.parse(e.data);

    if (m.type === 'status') {
        document.querySelectorAll('.dot[data-user="' + m.user + '"]')
            .forEach(d => d.classList.toggle('online', m.online));
        return;
    }

    if (!box) return;
    const inThisChat = (m.from === me && m.to === other ||
        m.from === other && m.to === me);

    if (!inThisChat) return;

    const p = document.createElement('p');
    const t = document.createElement('small');
    t.textContent = m.time;
    p.appendChild(t);
    p.appendChild(document.createTextNode(' '));
    const b = document.createElement('b');
    b.textContent = m.name + ': ';
    p.appendChild(b);
    p.appendChild(document.createTextNode(m.content));
    box.appendChild(p);
    p.scrollIntoView();
}
let ws = null;


function connect() {
    ws = new WebSocket('ws://' + location.hostname + ':8887');


    ws.onopen= () => {
        console.log('Websocket connected');
        retryDelay = 1000;
        if (!firstConnection) {
            location.reload();
            return;
        }
        firstConnection = false;
    };

    ws.onmessage = handleMessage;

    ws.onclose = (ev) => {
        if (leaving || retryScheduled) return;
        if (ev.code === 1008) {
            console.log("Websocket rejected, not retrying");
            return;
        }
        console.log('WebSocket closed, retrying in ' + retryDelay + ' ms');
        setTimeout(connect, retryDelay);
        retryDelay = Math.min(retryDelay * 2, MAX_DELAY);
    };

    ws.onerror = () => ws.close();
}

connect();