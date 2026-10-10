export const gradingEngineNamesMap = {
  Batch: 'Batch',
  BatchWithSubtasks: 'Batch with Subtasks',
  Interactive: 'Interactive',
  InteractiveWithSubtasks: 'Interactive with Subtasks',
  OutputOnly: 'Output Only',
  OutputOnlyWithSubtasks: 'Output Only with Subtasks',
  Functional: 'Functional',
  FunctionalWithSubtasks: 'Functional with Subtasks',
};

export function isInteractive(engine) {
  return engine.startsWith('Interactive');
}

export function isOutputOnly(engine) {
  return engine.startsWith('OutputOnly');
}
