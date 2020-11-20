export function isInteractive(engine) {
  return engine.startsWith('Interactive');
}

export function isOutputOnly(engine) {
  return engine.startsWith('OutputOnly');
}
