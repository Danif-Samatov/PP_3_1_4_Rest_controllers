async function api(url, {method = 'GET', body} = {}) {
    const headers = {Accept: 'application/json'};
    const upperMethod = method.toUpperCase();
    if (upperMethod !== 'GET' && upperMethod !== 'HEAD') {
        const tokenMeta = document.querySelector('meta[name="_csrf"]');
        const headerMeta = document.querySelector('meta[name="_csrf_header"]');
        if (tokenMeta && headerMeta) {
            headers[headerMeta.content] = tokenMeta.content;
        }
    }
    if (body !== undefined) {
        headers['Content-Type'] = 'application/json';
    }

    const response = await fetch(url, {
        method: upperMethod,
        credentials: 'same-origin',
        headers,
        body: body !== undefined ? JSON.stringify(body) : undefined
    });

    if (response.status === 204) {
        return null;
    }

    const contentType = response.headers.get('content-type') || '';
    const payload = contentType.includes('application/json') ? await response.json() : null;
    if (!response.ok) {
        const message = payload && (payload.message || payload.error)
            ? (payload.message || payload.error)
            : 'Request failed';
        throw new Error(message);
    }
    return payload;
}

function textCell(value) {
    const cell = document.createElement('td');
    cell.textContent = value == null ? '' : String(value);
    return cell;
}

function setAlert(id, message) {
    const alert = document.getElementById(id);
    if (!alert) {
        return;
    }
    alert.textContent = message || '';
    alert.classList.toggle('d-none', !message);
}
