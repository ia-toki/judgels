import { Grading } from '../gabriel/grading';
import { UserInfo } from '../jophiel/user';
import { SubmissionSource } from '../gabriel/submission';

export interface Submission {
  id: number;
  jid: string;
  userJid: string;
  problemJid: string;
  containerJid: string;
  gradingEngine: string;
  gradingLanguage: string;
  time: number;
  latestGrading?: Grading;
}

export interface SubmissionWithSource {
  submission: Submission;
  source: SubmissionSource;
}

export interface SubmissionWithSourceResponse {
  data: SubmissionWithSource;
  user: UserInfo;
  problemName: string;
  problemAlias: string;
  containerName: string;
}
