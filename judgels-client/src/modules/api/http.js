import { clearSession } from '../session';
import { BadRequestError, ForbiddenError, NotFoundError, RemoteError } from './error';

async function call(url, init) {
  let response;
  try {
    response = await fetch(url, init);
  } catch (error) {
    throw new RemoteError();
  }

  const json = response.json().catch(() => {
    return;
  });

  if (response.status === 400) {
    throw new BadRequestError(await json);
  }
  if (response.status === 401) {
    clearSession();
    window.location.replace('/');
    return new Promise(() => {});
  }
  if (response.status === 403) {
    throw new ForbiddenError(await json);
  }
  if (response.status === 404) {
    throw new NotFoundError(await json);
  }
  if (response.status < 200 || response.status >= 300) {
    throw new RemoteError(await json);
  }

  return json;
}

async function request(method, url, token, headers, body) {
  const authHeader = token ? { Authorization: `Bearer ${token}` } : {};

  const init = {
    method,
    mode: 'cors',
    headers: { ...headers, ...authHeader },
    body,
  };

  return call(url, init);
}

export async function get(url, token) {
  return request('GET', url, token);
}

export async function delete_(url, token) {
  return request('DELETE', url, token);
}

export async function post(url, token, body) {
  return request('POST', url, token, { 'Content-Type': 'application/json' }, JSON.stringify(body));
}

export async function put(url, token, body) {
  return request('PUT', url, token, { 'Content-Type': 'application/json' }, JSON.stringify(body));
}

export async function postMultipart(url, token, parts) {
  const body = new FormData();
  Object.keys(parts).forEach(part => body.append(part, parts[part]));

  return request('POST', url, token, {}, body);
}

export async function download(url, token) {
  const authHeader = token ? { Authorization: `Bearer ${token}` } : {};
  const init = {
    method: 'GET',
    headers: authHeader,
  };

  const res = await fetch(url, init);
  const filename = res.headers.get('Content-Disposition').match(/filename=(["']?)(.+)\1/i)[2];

  const blob = await res.blob();
  const objUrl = window.URL.createObjectURL(blob);

  let link = document.createElement('a');
  link.href = objUrl;
  link.download = filename;
  link.click();

  setTimeout(() => {
    window.URL.revokeObjectURL(objUrl);
  }, 250);
}
