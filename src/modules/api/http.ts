import { BadRequestError, ForbiddenError, NotFoundError, RemoteError, UnauthorizedError } from './error';

async function call(url: string, init: RequestInit): Promise<any> {
  let response: Response;
  try {
    response = await fetch(url, init);
  } catch (error) {
    throw new RemoteError();
  }

  if (response.status === 400) {
    throw new BadRequestError();
  }
  if (response.status === 401) {
    throw new UnauthorizedError();
  }
  if (response.status === 403) {
    throw new ForbiddenError();
  }
  if (response.status === 404) {
    throw new NotFoundError();
  }
  if (response.status < 200 || response.status >= 300) {
    throw new RemoteError();
  }

  return response.json().catch(() => {
    return;
  });
}

async function request(method: string, url: string, token?: string, headers?: any, body?: any): Promise<any> {
  const authHeader = token ? { Authorization: `Bearer ${token}` } : {};

  const init: RequestInit = {
    method,
    mode: 'cors',
    headers: { ...headers, ...authHeader },
    body,
  };

  return call(url, init);
}

export async function get(url: string, token?: string): Promise<any> {
  return request('GET', url, token);
}

export async function delete_(url: string, token?: string): Promise<any> {
  return request('DELETE', url, token);
}

export async function post(url: string, token?: string, body?: any): Promise<any> {
  return request('POST', url, token, { 'Content-Type': 'application/json' }, JSON.stringify(body));
}

export async function put(url: string, token?: string, body?: any): Promise<any> {
  return request('PUT', url, token, { 'Content-Type': 'application/json' }, JSON.stringify(body));
}

export async function postMultipart(url: string, token: string, file: File): Promise<any> {
  const body = new FormData();
  body.append('file', file);

  return request('POST', url, token, {}, body);
}
