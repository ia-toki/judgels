import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { connect } from 'react-redux';

import { AppState } from 'modules/store';
import { Contest } from 'modules/api/uriel/contest';
import { ContestModulesConfig } from 'modules/api/uriel/contestModule';
import { LoadingState } from 'components/LoadingState/LoadingState';

import { ContestEditConfigsTable } from '../ContestEditConfigsTable/ContestEditConfigsTable';
import { selectContest } from '../../../modules/contestSelectors';
import { contestModuleActions as injectedContestModuleActions } from '../../modules/contestModuleActions';

interface ContestEditConfigsTabProps {
  contest: Contest;
  onGetConfig: (contestJid: string) => Promise<ContestModulesConfig>;
  onUpsertConfig: (contestJid: string, config: ContestModulesConfig) => Promise<void>;
}

interface ContestEditConfigsTabState {
  config?: ContestModulesConfig;
  isEditing?: boolean;
}

class ContestEditConfigsTab extends React.Component<ContestEditConfigsTabProps, ContestEditConfigsTabState> {
  state: ContestEditConfigsTabState = {};

  async componentDidMount() {
    await this.refreshConfigs();
  }

  render() {
    return (
      <>
        <h4>
          Configs settings
          {this.renderEditButton()}
        </h4>
        <hr />
        {this.renderContent()}
      </>
    );
  }

  private refreshConfigs = async () => {
    const config = await this.props.onGetConfig(this.props.contest.jid);
    this.setState({ config });
  };

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
    const { config } = this.state;
    if (config === undefined) {
      return <LoadingState />;
    }
    // if (isEditing) {
    //   const initialValues: ContestEditConfigsFormData = {
    //     Configs: Configs,
    //   };
    //   const formProps = {
    //     onCancel: this.toggleEdit,
    //   };
    //   return (
    //     <ContestEditConfigsForm initialValues={initialValues} onSubmit={this.updateContestConfigs} {...formProps} />
    //   );
    // }
    return <ContestEditConfigsTable config={config} />;
  };

  // private updateContestConfigs = async (data: ContestEditConfigsFormData) => {
  //   await this.props.onUpdateContestConfigs(this.props.contest.jid, data.Configs);
  //   await this.refreshConfigs();
  //   this.toggleEdit();
  // };

  private toggleEdit = () => {
    this.setState((prevState: ContestEditConfigsTabState) => ({
      isEditing: !prevState.isEditing,
    }));
  };
}

export function createContestEditConfigsTab(contestModuleActions) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state),
  });
  const mapDispatchToProps = {
    onGetConfig: contestModuleActions.getConfig,
    onUpsertConfig: contestModuleActions.upsertConfig,
  };
  return connect(mapStateToProps, mapDispatchToProps)(ContestEditConfigsTab);
}

export default createContestEditConfigsTab(injectedContestModuleActions);
