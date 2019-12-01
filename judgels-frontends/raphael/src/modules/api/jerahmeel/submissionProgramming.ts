import { Page } from '../pagination';
import { ProfilesMap } from '../jophiel/profile';
import { Submission } from '../sandalphon/submissionProgramming';
import { SubmissionConfig } from './submission';

export interface SubmissionsResponse {
  data: Page<Submission>;
  config: SubmissionConfig;
  profilesMap: ProfilesMap;
  problemAliasesMap: { [problemJid: string]: string };
}
