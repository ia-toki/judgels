import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { connect } from 'react-redux';

import { AppState } from 'modules/store';
import { Contest } from 'modules/api/uriel/contest';
import { ContestModulesConfig } from 'modules/api/uriel/contestModule';
import { allLanguagesAllowed, LanguageRestriction } from 'modules/api/gabriel/language';
import { LoadingState } from 'components/LoadingState/LoadingState';
import { formatDuration, parseDuration } from 'utils/duration';

import { ContestEditConfigsTable } from '../ContestEditConfigsTable/ContestEditConfigsTable';
import ContestEditConfigsForm, { ContestEditConfigsFormData } from '../ContestEditConfigsForm/ContestEditConfigsForm';
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
    await this.refreshConfig();
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

  private refreshConfig = async () => {
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
    const { config, isEditing } = this.state;
    if (config === undefined) {
      return <LoadingState />;
    }
    if (isEditing) {
      const {
        icpcStyle,
        ioiStyle,
        scoreboard,
        clarificationTimeLimit,
        delayedGrading,
        frozenScoreboard,
        virtual,
      } = config;

      let initialValues: ContestEditConfigsFormData = {
        scoreboardIsIncognito: scoreboard.isIncognitoScoreboard,
      };
      if (icpcStyle) {
        initialValues = {
          ...initialValues,
          icpcAllowAllLanguages: allLanguagesAllowed(icpcStyle.languageRestriction),
          icpcAllowedLanguages: this.fromLanguageRestriction(icpcStyle.languageRestriction),
          icpcWrongSubmissionPenalty: '' + icpcStyle.wrongSubmissionPenalty,
        };
      }
      if (ioiStyle) {
        initialValues = {
          ...initialValues,
          ioiAllowAllLanguages: allLanguagesAllowed(ioiStyle.languageRestriction),
          ioiAllowedLanguages: this.fromLanguageRestriction(ioiStyle.languageRestriction),
          ioiUsingLastAffectingPenalty: ioiStyle.usingLastAffectingPenalty,
        };
      }
      if (clarificationTimeLimit) {
        initialValues = {
          ...initialValues,
          clarificationTimeLimitDuration: formatDuration(clarificationTimeLimit.clarificationDuration),
        };
      }
      if (delayedGrading) {
        initialValues = { ...initialValues, delayedGradingDuration: formatDuration(delayedGrading.delayDuration) };
      }
      if (frozenScoreboard) {
        initialValues = {
          ...initialValues,
          frozenScoreboardFreezeTime: formatDuration(frozenScoreboard.scoreboardFreezeTime),
          frozenScoreboardIsOfficialAllowed: frozenScoreboard.isOfficialScoreboardAllowed,
        };
      }
      if (virtual) {
        initialValues = { ...initialValues, virtualDuration: formatDuration(virtual.virtualDuration) };
      }

      const formProps = {
        config,
        onCancel: this.toggleEdit,
      };
      return <ContestEditConfigsForm initialValues={initialValues} onSubmit={this.upsertConfig} {...formProps} />;
    }
    return <ContestEditConfigsTable config={config} />;
  };

  private upsertConfig = async (data: ContestEditConfigsFormData) => {
    const {
      icpcStyle,
      ioiStyle,
      clarificationTimeLimit,
      delayedGrading,
      frozenScoreboard,
      virtual,
    } = this.state.config!;

    let config: ContestModulesConfig = {
      scoreboard: {
        isIncognitoScoreboard: data.scoreboardIsIncognito,
      },
    };
    if (icpcStyle) {
      const allowedLanguageNames = data.icpcAllowAllLanguages ? [] : Object.keys(data.icpcAllowedLanguages!);
      config = {
        ...config,
        icpcStyle: {
          languageRestriction: { allowedLanguageNames },
          wrongSubmissionPenalty: +data.icpcWrongSubmissionPenalty!,
        },
      };
    }
    if (ioiStyle) {
      const allowedLanguageNames = data.ioiAllowAllLanguages ? [] : Object.keys(data.ioiAllowedLanguages!);
      config = {
        ...config,
        ioiStyle: {
          languageRestriction: { allowedLanguageNames },
          usingLastAffectingPenalty: data.ioiUsingLastAffectingPenalty!,
        },
      };
    }
    if (clarificationTimeLimit) {
      config = {
        ...config,
        clarificationTimeLimit: { clarificationDuration: parseDuration(data.clarificationTimeLimitDuration!) },
      };
    }
    if (delayedGrading) {
      config = { ...config, delayedGrading: { delayDuration: parseDuration(data.delayedGradingDuration!) } };
    }
    if (frozenScoreboard) {
      config = {
        ...config,
        frozenScoreboard: {
          scoreboardFreezeTime: parseDuration(data.frozenScoreboardFreezeTime!),
          isOfficialScoreboardAllowed: data.frozenScoreboardIsOfficialAllowed!,
        },
      };
    }
    if (virtual) {
      config = { ...config, virtual: { virtualDuration: parseDuration(data.virtualDuration!) } };
    }

    await this.props.onUpsertConfig(this.props.contest.jid, config);
    await this.refreshConfig();
    this.toggleEdit();
  };

  private fromLanguageRestriction = (r: LanguageRestriction) => {
    return Object.assign({}, ...r.allowedLanguageNames.map(l => ({ [l]: true })));
  };

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
