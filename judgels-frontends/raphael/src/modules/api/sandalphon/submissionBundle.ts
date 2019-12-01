import { Page } from '../pagination';
import { Profile } from '../jophiel/profile';
import { ItemType } from './problemBundle';

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

export interface ItemSubmissionData {
  containerJid: string;
  problemJid: string;
  itemJid: string;
  answer: string;
}

export interface ItemSubmissionsResponse {
  data: Page<ItemSubmission>;
  profilesMap: { [id: string]: Profile };
  problemAliasesMap: { [id: string]: string };
  itemNumbersMap: { [itemJid: string]: number };
  itemTypesMap: { [itemJid: string]: ItemType };
}

export interface SubmissionSummaryResponse {
  profile: Profile;
  itemJidsByProblemJid: { [problemJid: string]: string[] };
  submissionsByItemJid: { [itemJid: string]: ItemSubmission };
  problemAliasesMap: { [id: string]: string };
  problemNamesMap: { [id: string]: string };
  itemTypesMap: { [itemJid: string]: ItemType };
}
