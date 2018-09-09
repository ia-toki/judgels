import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { connect } from 'react-redux';

import { AppState } from 'modules/store';
import { Contest, ContestUpdateData } from 'modules/api/uriel/contest';

import { ContestEditGeneralTable } from '../ContestEditGeneralTable/ContestEditGeneralTable';
import ContestEditGeneralForm from '../ContestEditGeneralForm/ContestEditGeneralForm';
import { selectContest } from '../../../modules/contestSelectors';
import { contestActions as injectedContestActions } from '../../../modules/contestActions';

interface ContestEditGeneralTabProps {
  contest: Contest;
  onUpdateContest: (contestJid: string, data: ContestUpdateData) => void;
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
      const initialValues: ContestUpdateData = {
        slug: contest.slug,
        name: contest.name,
        style: contest.style,
        beginTime: contest.beginTime,
        duration: contest.duration,
      };
      const formProps = {
        onCancel: this.toggleEdit,
      };
      return <ContestEditGeneralForm initialValues={initialValues} onSubmit={this.updateContest} {...formProps} />;
    }
    return <ContestEditGeneralTable contest={contest} />;
  };

  private updateContest = async (data: ContestUpdateData) => {
    await this.props.onUpdateContest(this.props.contest.jid, data);
    this.toggleEdit();
  };

  private toggleEdit = () => {
    this.setState((prevState: ContestEditGeneralTabState) => ({
      isEditing: !prevState.isEditing,
    }));
  };
}

function createContestEditGeneralTab(contestActions) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state),
  });
  const mapDispatchToProps = {
    onUpdateContest: contestActions.updateContest,
  };
  return connect(mapStateToProps, mapDispatchToProps)(ContestEditGeneralTab);
}

export default createContestEditGeneralTab(injectedContestActions);
