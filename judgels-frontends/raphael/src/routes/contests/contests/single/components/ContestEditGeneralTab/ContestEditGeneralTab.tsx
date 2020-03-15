import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { connect } from 'react-redux';

import { AppState } from '../../../../../../modules/store';
import { Contest, ContestStyle, ContestUpdateData } from '../../../../../../modules/api/uriel/contest';
import { formatDuration, parseDuration } from '../../../../../../utils/duration';

import { ContestEditGeneralTable } from '../ContestEditGeneralTable/ContestEditGeneralTable';
import ContestEditGeneralForm, { ContestEditGeneralFormData } from '../ContestEditGeneralForm/ContestEditGeneralForm';
import { selectContest } from '../../../modules/contestSelectors';
import * as contestActions from '../../../modules/contestActions';
import * as contestWebActions from '../../modules/contestWebActions';

interface ContestEditGeneralTabProps {
  contest: Contest;
  onGetContestByJidWithWebConfig: (contestJid: string) => Promise<any>;
  onUpdateContest: (contestJid: string, contestSlug: string, data: ContestUpdateData) => Promise<any>;
}

interface ContestEditGeneralTabState {
  isEditing?: boolean;
}

class ContestEditGeneralTab extends React.Component<ContestEditGeneralTabProps, ContestEditGeneralTabState> {
  state: ContestEditGeneralTabState = {};

  render() {
    return (
      <>
        <h4>
          General settings
          {this.renderEditButton()}
        </h4>
        <hr />
        {this.renderContent()}
      </>
    );
  }

  private renderEditButton = () => {
    return (
      !this.state.isEditing && (
        <Button small className="right-action-button" intent={Intent.PRIMARY} icon="edit" onClick={this.toggleEdit}>
          Edit
        </Button>
      )
    );
  };

  private renderContent = () => {
    const { contest } = this.props;
    if (this.state.isEditing) {
      const initialValues: ContestEditGeneralFormData = {
        slug: contest.slug,
        name: contest.name,
        style: contest.style,
        beginTime: new Date(contest.beginTime),
        duration: formatDuration(contest.duration),
      };
      const formProps = {
        onCancel: this.toggleEdit,
      };
      return <ContestEditGeneralForm initialValues={initialValues} onSubmit={this.updateContest} {...formProps} />;
    }
    return <ContestEditGeneralTable contest={contest} />;
  };

  private updateContest = async (data: ContestEditGeneralFormData) => {
    const updateData: ContestUpdateData = {
      slug: data.slug,
      name: data.name,
      style: data.style as ContestStyle,
      beginTime: data.beginTime.getTime(),
      duration: parseDuration(data.duration),
    };
    await this.props.onUpdateContest(this.props.contest.jid, this.props.contest.slug, updateData);
    await this.props.onGetContestByJidWithWebConfig(this.props.contest.jid);
    this.toggleEdit();
  };

  private toggleEdit = () => {
    this.setState((prevState: ContestEditGeneralTabState) => ({
      isEditing: !prevState.isEditing,
    }));
  };
}

const mapStateToProps = (state: AppState) => ({
  contest: selectContest(state),
});
const mapDispatchToProps = {
  onGetContestByJidWithWebConfig: contestWebActions.getContestByJidWithWebConfig,
  onUpdateContest: contestActions.updateContest,
};
export default connect(mapStateToProps, mapDispatchToProps)(ContestEditGeneralTab);
