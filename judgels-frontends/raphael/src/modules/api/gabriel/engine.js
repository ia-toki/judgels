export function isInteractive(engine: string): boolean {
  return engine.startsWith('Interactive');
}

export function isOutputOnly(engine: string): boolean {
  return engine.startsWith('OutputOnly');
}
