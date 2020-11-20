import { Intent } from '@blueprintjs/core';
import * as React from 'react';
import { connect } from 'react-redux';

import { allModules } from '../../../../../../modules/api/uriel/contestModule';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { selectContest } from '../../../modules/contestSelectors';
import { ContestModuleCard } from '../ContestModuleCard/ContestModuleCard';
import * as contestWebActions from '../../modules/contestWebActions';
import * as contestModuleActions from '../../modules/contestModuleActions';

class ContestEditModulesTab extends React.Component {
  state = {
    modules: undefined,
  };

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

  renderContent = () => {
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

  renderEnabledModules = enabledModules => {
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

  renderDisabledModules = disabledModules => {
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

  enableModule = async type => {
    await this.props.onEnableModule(this.props.contest.jid, type);
    await Promise.all([this.props.onGetContestByJidWithWebConfig(this.props.contest.jid), this.refreshModules()]);
  };

  disableModule = async type => {
    await this.props.onDisableModule(this.props.contest.jid, type);
    await Promise.all([this.props.onGetContestByJidWithWebConfig(this.props.contest.jid), this.refreshModules()]);
  };

  refreshModules = async () => {
    const modules = await this.props.onGetModules(this.props.contest.jid);
    this.setState({ modules });
  };
}

const mapStateToProps = state => ({
  contest: selectContest(state),
});
const mapDispatchToProps = {
  onGetContestByJidWithWebConfig: contestWebActions.getContestByJidWithWebConfig,
  onGetModules: contestModuleActions.getModules,
  onEnableModule: contestModuleActions.enableModule,
  onDisableModule: contestModuleActions.disableModule,
};
export default connect(mapStateToProps, mapDispatchToProps)(ContestEditModulesTab);
