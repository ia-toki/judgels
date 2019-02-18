export interface ContestSubmissionConfig {
  canSupervise: boolean;
  canManage: boolean;
  userJids: string[];
  problemJids: string[];
}
