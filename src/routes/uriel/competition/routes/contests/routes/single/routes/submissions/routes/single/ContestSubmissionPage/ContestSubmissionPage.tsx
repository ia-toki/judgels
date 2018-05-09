import * as React from 'react';
import { RouteComponentProps } from 'react-router';

import { Contest } from '../../../../../../../../../../../../modules/api/uriel/contest';

export interface ContestSubmissionPageProps extends RouteComponentProps<{ submissionId: string }> {
  contest: Contest;
}

export class ContestSubmissionPage extends React.Component<ContestSubmissionPageProps> {
  render() {
    return <div>This is a submission.</div>;
  }
}

export default ContestSubmissionPage;
