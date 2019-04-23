export enum Verdict {
  PENDING_REGRADE = 'PENDING_REGRADE',
  PENDING_MANUAL_GRADING = 'PENDING_MANUAL_GRADING',
  INTERNAL_ERROR = 'INTERNAL_ERROR',
  OK = 'OK',
  ACCEPTED = 'ACCEPTED',
  WRONG_ANSWER = 'WRONG_ANSWER',
}

export interface Grading {
  verdict: Verdict;
  score?: number;
}

export interface ItemSubmission {
  id: number;
  jid: string;
  containerJid: string;
  problemJid: string;
  grading?: Grading;
  itemJid: string;
  answer: string;
  userJid: string;
  time: number;
}
