import { Icon } from '@blueprintjs/core';
import * as React from 'react';

import { FormattedDuration } from '../../../../../../components/FormattedDuration/FormattedDuration';
import { FormTable, FormTableRow } from '../../../../../../components/forms/FormTable/FormTable';
import {
  ClarificationTimeLimitModuleConfig,
  ContestModulesConfig,
  DivisionModuleConfig,
  FrozenScoreboardModuleConfig,
  ExternalScoreboardModuleConfig,
  IcpcStyleModuleConfig,
  IoiStyleModuleConfig,
  GcjStyleModuleConfig,
  ScoreboardModuleConfig,
  VirtualModuleConfig,
} from '../../../../../../modules/api/uriel/contestModule';
import { getGradingLanguageName, LanguageRestriction } from '../../../../../../modules/api/gabriel/language';

import './ContestEditConfigsTable.css';

export interface ContestEditConfigsTableProps {
  config: ContestModulesConfig;
}

export class ContestEditConfigsTable extends React.Component<ContestEditConfigsTableProps> {
  render() {
    const { config } = this.props;
    return (
      <div className="contest-edit-dialog__content">
        {config.icpcStyle && this.renderIcpcStyleConfig(config.icpcStyle)}
        {config.ioiStyle && this.renderIoiStyleConfig(config.ioiStyle)}
        {config.gcjStyle && this.renderGcjStyleConfig(config.gcjStyle)}
        {config.clarificationTimeLimit && this.renderClarificationTimeLimitConfig(config.clarificationTimeLimit)}
        {config.division && this.renderDivisionConfig(config.division)}
        {config.scoreboard && this.renderScoreboardConfig(config.scoreboard)}
        {config.frozenScoreboard && this.renderFrozenScoreboardConfig(config.frozenScoreboard)}
        {config.externalScoreboard && this.renderExternalScoreboardConfig(config.externalScoreboard)}
        {config.virtual && this.renderVirtualConfig(config.virtual)}
      </div>
    );
  }

  private renderIcpcStyleConfig = (config: IcpcStyleModuleConfig) => {
    const rows: FormTableRow[] = [
      {
        key: 'languageRestriction',
        title: 'Allowed languages',
        value: this.formatLanguageRestriction(config.languageRestriction),
      },
      { key: 'wrongSubmissionPenalty', title: 'Wrong submission penalty', value: config.wrongSubmissionPenalty },
    ];
    return (
      <div className="contest-edit-configs-table__config">
        <h4>ICPC style config</h4>
        <FormTable rows={rows} keyClassName="contest-edit-configs-table__key" />
        <hr />
      </div>
    );
  };

  private renderIoiStyleConfig = (config: IoiStyleModuleConfig) => {
    const rows: FormTableRow[] = [
      {
        key: 'languageRestriction',
        title: 'Allowed languages',
        value: this.formatLanguageRestriction(config.languageRestriction),
      },
      {
        key: 'usingLastAffectingPenalty',
        title: 'Using last affecting penalty?',
        value: <Icon icon={config.usingLastAffectingPenalty ? 'small-tick' : 'small-cross'} />,
      },
      {
        key: 'usingMaxScorePerSubtask',
        title: 'Using max score per subtask?',
        value: <Icon icon={config.usingMaxScorePerSubtask ? 'small-tick' : 'small-cross'} />,
      },
    ];
    return (
      <div className="contest-edit-configs-table__config">
        <h4>IOI style config</h4>
        <FormTable rows={rows} keyClassName="contest-edit-configs-table__key" />
        <hr />
      </div>
    );
  };

  private renderGcjStyleConfig = (config: GcjStyleModuleConfig) => {
    const rows: FormTableRow[] = [
      {
        key: 'languageRestriction',
        title: 'Allowed languages',
        value: this.formatLanguageRestriction(config.languageRestriction),
      },
      { key: 'wrongSubmissionPenalty', title: 'Wrong submission penalty', value: config.wrongSubmissionPenalty },
    ];
    return (
      <div className="contest-edit-configs-table__config">
        <h4>GCJ style config</h4>
        <FormTable rows={rows} keyClassName="contest-edit-configs-table__key" />
        <hr />
      </div>
    );
  };

  private renderClarificationTimeLimitConfig = (config: ClarificationTimeLimitModuleConfig) => {
    const rows: FormTableRow[] = [
      {
        key: 'clarificationDuration',
        title: 'Clarification duration',
        value: (
          <>
            <FormattedDuration value={config.clarificationDuration} /> (since contest start time)
          </>
        ),
      },
    ];
    return (
      <div className="contest-edit-configs-table__config">
        <h4>Clarification time limit config</h4>
        <FormTable rows={rows} keyClassName="contest-edit-configs-table__key" />
        <hr />
      </div>
    );
  };

  private renderDivisionConfig = (config: DivisionModuleConfig) => {
    const rows: FormTableRow[] = [
      {
        key: 'divisionDivision',
        title: 'Division',
        value: <>{config.division}</>,
      },
    ];
    return (
      <div className="contest-edit-configs-table__config">
        <h4>Division config</h4>
        <FormTable rows={rows} keyClassName="contest-edit-configs-table__key" />
        <hr />
      </div>
    );
  };

  private renderScoreboardConfig = (config: ScoreboardModuleConfig) => {
    const rows: FormTableRow[] = [
      {
        key: 'isIncognitoScoreboard',
        title: 'Incognito scoreboard?',
        value: <Icon icon={config.isIncognitoScoreboard ? 'small-tick' : 'small-cross'} />,
      },
    ];
    return (
      <div className="contest-edit-configs-table__config">
        <h4>Scoreboard config</h4>
        <FormTable rows={rows} keyClassName="contest-edit-configs-table__key" />
        <hr />
      </div>
    );
  };

  private renderFrozenScoreboardConfig = (config: FrozenScoreboardModuleConfig) => {
    const rows: FormTableRow[] = [
      {
        key: 'scoreboardFreezeTime',
        title: 'Freeze time',
        value: (
          <>
            <FormattedDuration value={config.scoreboardFreezeTime} />
            (before contest end time)
          </>
        ),
      },
      {
        key: 'isIncognitoScoreboard',
        title: 'Is now unfrozen?',
        value: <Icon icon={config.isOfficialScoreboardAllowed ? 'small-tick' : 'small-cross'} />,
      },
    ];
    return (
      <div className="contest-edit-configs-table__config">
        <h4>Freezable scoreboard config</h4>
        <FormTable rows={rows} keyClassName="contest-edit-configs-table__key" />
        <hr />
      </div>
    );
  };

  private renderExternalScoreboardConfig = (config: ExternalScoreboardModuleConfig) => {
    const rows: FormTableRow[] = [
      {
        key: 'receiverUrl',
        title: 'Receiver URL',
        value: config.receiverUrl,
      },
      {
        key: 'receiverSecret',
        title: 'Receiver secret',
        value: config.receiverSecret,
      },
    ];
    return (
      <div className="contest-edit-configs-table__config">
        <h4>External scoreboard config</h4>
        <FormTable rows={rows} keyClassName="contest-edit-configs-table__key" />
        <hr />
      </div>
    );
  };

  private renderVirtualConfig = (config: VirtualModuleConfig) => {
    const rows: FormTableRow[] = [
      {
        key: 'virtualDuration',
        title: 'Virtual contest duration',
        value: <FormattedDuration value={config.virtualDuration} />,
      },
    ];
    return (
      <div className="contest-edit-configs-table__config">
        <h4>Virtual contest config</h4>
        <FormTable rows={rows} keyClassName="contest-edit-configs-table__key" />
        <hr />
      </div>
    );
  };

  private formatLanguageRestriction = (restriction: LanguageRestriction) => {
    const languages = restriction.allowedLanguageNames;
    if (!languages || languages.length === 0) {
      return '(all)';
    }
    return languages
      .sort()
      .map(getGradingLanguageName)
      .join(', ');
  };
}
