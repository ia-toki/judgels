import { Classes, Dialog } from '@blueprintjs/core';
import { Component } from 'react';
import { connect } from 'react-redux';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { GradingVerdictTag } from '../../../../../../components/GradingVerdictTag/GradingVerdictTag';
import { SubmissionDetails } from '../../../../../../components/SubmissionDetails/Programming/SubmissionDetails';
import { selectStatementLanguage } from '../../../../../../modules/webPrefs/webPrefsSelectors';
import { selectContest } from '../../../modules/contestSelectors';

import * as contestScoreboardActions from '../modules/contestScoreboardActions';

import './ContestUserProblemSubmissionsDialog.scss';

class ContestUserProblemSubmissionsDialog extends Component {
  state = {
    submissions: undefined,
    submissionSourcesById: {},
  };

  async componentDidMount() {
    const response = await this.props.onGetUserProblemSubmissions(
      this.props.contest.jid,
      this.props.userJid,
      this.props.problemJid
    );

    const { data: submissions, latestSubmissionSource } = response;

    this.setState({
      submissions,
    });

    if (submissions.length > 0) {
      this.setState({
        submissionSourcesById: {
          [submissions[0].id]: latestSubmissionSource,
        },
      });
    }
  }

  loadSubmissionSource = async submissionId => {
    this.setState(prevState => ({
      submissionSourcesById: {
        ...prevState.submissionSourcesById,
        [submissionId]: null,
      },
    }));

    const submissionWithSource = await this.props.onGetSubmissionWithSource(
      this.props.contest.jid,
      submissionId,
      this.props.statementLanguage
    );

    this.setState(prevState => ({
      submissionSourcesById: {
        ...prevState.submissionSourcesById,
        [submissionId]: submissionWithSource.data.source,
      },
    }));
  };

  renderContent = () => {
    const { submissions } = this.state;
    if (!submissions) {
      return null;
    }

    return submissions.map((submission, idx) => (
      <ContentCard key={submission.id}>
        <details open={idx === 0}>
          <summary>
            <h5>
              <span className="details-heading">Submission #{submission.id}</span>
              <GradingVerdictTag grading={submission.latestGrading} />
            </h5>
          </summary>

          <div className="details-content">
            <SubmissionDetails
              submission={submission}
              source={this.state.submissionSourcesById[submission.id]}
              onClickViewSource={() => this.loadSubmissionSource(submission.id)}
            />
          </div>
        </details>
      </ContentCard>
    ));
  };

  render() {
    const { onClose, title } = this.props;

    return (
      <Dialog
        className="contest-user-problem-submissions-dialog"
        isOpen
        onClose={onClose}
        title={title}
        canOutsideClickClose={true}
        enforceFocus={true}
      >
        <div className={Classes.DIALOG_BODY}>{this.renderContent()}</div>
      </Dialog>
    );
  }
}

const mapStateToProps = state => ({
  contest: selectContest(state),
  statementLanguage: selectStatementLanguage(state),
});

const mapDispatchToProps = {
  onGetSubmissionWithSource: contestScoreboardActions.getSubmissionWithSource,
  onGetUserProblemSubmissions: contestScoreboardActions.getUserProblemSubmissions,
};

export default connect(mapStateToProps, mapDispatchToProps)(ContestUserProblemSubmissionsDialog);
