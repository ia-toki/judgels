export interface Verdict {
  code: string;
  name: string;
}

export enum SandboxExecutionStatus {
  ZeroExitCode = 'ZERO_EXIT_CODE',
  NonzeroExitCode = 'NONZERO_EXIT_CODE',
  KilledOnSignal = 'KILLED_ON_SIGNAL',
  TimedOut = 'TIMED_OUT',
  InternalError = 'INTERNAL_ERROR',
}

export interface SandboxExecutionResult {
  status: SandboxExecutionStatus;
  time: number;
  memory: number;
  message: string;
}

export interface TestCaseResult {
  verdict: Verdict;
  score: string;
  executionResult?: SandboxExecutionResult;
  subtaskIds: number[];
}

export interface TestGroupResult {
  id: number;
  testCaseResults: TestCaseResult[];
}
export interface SubtaskResult {
  id: number;
  verdict: Verdict;
  score: number;
}

export interface GradingResultDetails {
  compilationOutputs: { [sourceKey: string]: string };
  testDataResults: TestGroupResult[];
  subtaskResults: SubtaskResult[];
}

export interface Grading {
  id: number;
  jid: string;
  verdict: string;
  score: number;
  details?: GradingResultDetails;
}
