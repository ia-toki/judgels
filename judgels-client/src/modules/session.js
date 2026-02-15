import { useSyncExternalStore } from 'react';

const STORAGE_KEY = 'persist:session';
const defaultSession = { token: undefined, user: undefined };

function loadFromStorage() {
  const raw = localStorage.getItem(STORAGE_KEY);
  if (!raw) {
    return defaultSession;
  }

  try {
    const parsed = JSON.parse(raw);
    const token = JSON.parse(parsed.token);
    const user = JSON.parse(parsed.user);
    return { token: token || undefined, user: user || undefined };
  } catch {
    return defaultSession;
  }
}

function saveToStorage(session) {
  localStorage.setItem(
    STORAGE_KEY,
    JSON.stringify({
      token: JSON.stringify(session.token || null),
      user: JSON.stringify(session.user || null),
    })
  );
}

let currentSession = loadFromStorage();
let listeners = new Set();

function emitChange() {
  saveToStorage(currentSession);
  listeners.forEach(l => l());
}

export function getToken() {
  return currentSession.token;
}

export function getUser() {
  return currentSession.user;
}

export function setSession(token, user) {
  currentSession = { token, user };
  emitChange();
}

export function clearSession() {
  currentSession = defaultSession;
  localStorage.removeItem(STORAGE_KEY);
  listeners.forEach(l => l());
}

export function useSession() {
  const session = useSyncExternalStore(
    cb => {
      listeners.add(cb);
      return () => listeners.delete(cb);
    },
    () => currentSession
  );
  return {
    token: session.token,
    user: session.user,
    isLoggedIn: !!session.token,
  };
}
