import { Intent } from '@blueprintjs/core';
import * as React from 'react';
import { connect } from 'react-redux';

import { AppState } from 'modules/store';
import { Contest } from 'modules/api/uriel/contest';
import { allModules, ContestModuleType } from 'modules/api/uriel/contestModule';
import { LoadingState } from 'components/LoadingState/LoadingState';

import { selectContest } from '../../../modules/contestSelectors';
import { contestWebActions as injectedContestWebActions } from '../../modules/contestWebActions';
import { contestModuleActions as injectedContestModuleActions } from '../../modules/contestModuleActions';
import { ContestModuleCard } from '../ContestModuleCard/ContestModuleCard';

interface ContestEditModulesTabProps {
  contest: Contest;
  onGetContestByJidWithWebConfig: (contestJid: string) => Promise<void>;
  onGetModules: (contestJid: string) => Promise<ContestModuleType[]>;
  onEnableModule: (contestJid: string, type: ContestModuleType) => Promise<void>;
  onDisableModule: (contestJid: string, type: ContestModuleType) => Promise<void>;
}

interface ContestEditModulesTabState {
  modules?: ContestModuleType[];
}

class ContestEditModulesTab extends React.Component<ContestEditModulesTabProps, ContestEditModulesTabState> {
  state: ContestEditModulesTabState = {};

  async componentDidMount() {
    await this.refreshModules();
  }

  render() {
    return (
      <>
        <h4>Modules settings</h4>
        <hr />
        {this.renderContent()}
      </>
    );
  }

  private renderContent = () => {
    const { modules } = this.state;

    if (!modules) {
      return <LoadingState />;
    }

    const enabledModules = allModules.filter(m => modules.indexOf(m) !== -1);
    const disabledModules = allModules.filter(m => modules.indexOf(m) === -1);

    return (
      <div className="contest-edit-dialog__content">
        {this.renderEnabledModules(enabledModules)}
        <hr />
        {this.renderDisabledModules(disabledModules)}
      </div>
    );
  };

  private renderEnabledModules = (enabledModules: ContestModuleType[]) => {
    if (enabledModules.length === 0) {
      return (
        <p>
          <small>No enabled modules.</small>
        </p>
      );
    }

    return enabledModules.map(module => (
      <ContestModuleCard
        key={module}
        type={module}
        intent={Intent.PRIMARY}
        buttonIntent={Intent.NONE}
        buttonText={'Disable'}
        buttonOnClick={this.disableModule}
        buttonIsLoading={false}
        buttonIsDisabled={false}
      />
    ));
  };

  private renderDisabledModules = (disabledModules: ContestModuleType[]) => {
    if (disabledModules.length === 0) {
      return (
        <p>
          <small>No disabled modules.</small>
        </p>
      );
    }

    return disabledModules.map(module => (
      <ContestModuleCard
        key={module}
        type={module}
        intent={Intent.NONE}
        buttonIntent={Intent.PRIMARY}
        buttonText={'Enable'}
        buttonOnClick={this.enableModule}
        buttonIsLoading={false}
        buttonIsDisabled={false}
      />
    ));
  };

  private enableModule = async (type: ContestModuleType) => {
    await this.props.onEnableModule(this.props.contest.jid, type);
    await Promise.all([this.props.onGetContestByJidWithWebConfig(this.props.contest.jid), this.refreshModules()]);
  };

  private disableModule = async (type: ContestModuleType) => {
    await this.props.onDisableModule(this.props.contest.jid, type);
    await Promise.all([this.props.onGetContestByJidWithWebConfig(this.props.contest.jid), this.refreshModules()]);
  };

  private refreshModules = async () => {
    const modules = await this.props.onGetModules(this.props.contest.jid);
    this.setState({ modules });
  };
}

export function createContestEditModulesTab(contestWebActions, contestModuleActions) {
  const mapStateToProps = (state: AppState) => ({
    contest: selectContest(state),
  });
  const mapDispatchToProps = {
    onGetContestByJidWithWebConfig: contestWebActions.getContestByJidWithWebConfig,
    onGetModules: contestModuleActions.getModules,
    onEnableModule: contestModuleActions.enableModule,
    onDisableModule: contestModuleActions.disableModule,
  };
  return connect(mapStateToProps, mapDispatchToProps)(ContestEditModulesTab);
}

export default createContestEditModulesTab(injectedContestWebActions, injectedContestModuleActions);
