import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { connect } from 'react-redux';

import { AppState } from 'modules/store';
import { Contest, ContestData } from 'modules/api/uriel/contest';

import { ContestEditGeneralTable } from '../ContestEditGeneralTable/ContestEditGeneralTable';
import ContestEditGeneralForm from '../ContestEditGeneralForm/ContestEditGeneralForm';
import { selectContest } from '../../../modules/contestSelectors';
import { contestActions as injectedContestActions } from '../../../modules/contestActions';

interface ContestEditGeneralTabProps {
  contest: Contest;
  onUpdateContest: (data: ContestData) => void;
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
      const formProps = {
        onCancel: this.toggleEdit,
      };
      return <ContestEditGeneralForm initialValues={contest} {...formProps} />;
    }
    return <ContestEditGeneralTable contest={contest} />;
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
