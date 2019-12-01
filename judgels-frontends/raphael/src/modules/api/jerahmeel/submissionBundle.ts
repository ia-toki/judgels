import { Page } from '../../../modules/api/pagination';
import { ItemSubmission } from '../../../modules/api/sandalphon/submissionBundle';
import { SubmissionConfig } from './submission';
import { Profile } from '../../../modules/api/jophiel/profile';
import { ItemType } from '../sandalphon/problemBundle';

export interface ItemSubmissionData {
  containerJid: string;
  problemJid: string;
  itemJid: string;
  answer: string;
}

export interface ItemSubmissionsResponse {
  data: Page<ItemSubmission>;
  config: SubmissionConfig;
  profilesMap: { [id: string]: Profile };
  problemAliasesMap: { [id: string]: string };
  itemNumbersMap: { [itemJid: string]: number };
  itemTypesMap: { [itemJid: string]: ItemType };
}

export interface AnswerSummaryResponse {
  profile: Profile;
  config: SubmissionConfig;
  itemJidsByProblemJid: { [problemJid: string]: string[] };
  submissionsByItemJid: { [itemJid: string]: ItemSubmission };
  problemAliasesMap: { [id: string]: string };
  problemNamesMap: { [id: string]: string };
  itemTypesMap: { [itemJid: string]: ItemType };
}
