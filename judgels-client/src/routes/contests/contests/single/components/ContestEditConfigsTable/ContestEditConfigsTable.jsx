import { SmallCross, SmallTick } from '@blueprintjs/icons';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { FormattedDuration } from '../../../../../../components/FormattedDuration/FormattedDuration';
import { HtmlText } from '../../../../../../components/HtmlText/HtmlText';
import { FormTable } from '../../../../../../components/forms/FormTable/FormTable';
import { getGradingLanguageName } from '../../../../../../modules/api/gabriel/language.js';

import './ContestEditConfigsTable.scss';

export function ContestEditConfigsTable({ config }) {
  const renderTrocStyleConfig = ({ languageRestriction, wrongSubmissionPenalty }) => {
    const rows = [
      {
        key: 'languageRestriction',
        title: 'Allowed languages',
        value: formatLanguageRestriction(languageRestriction),
      },
      { key: 'wrongSubmissionPenalty', title: 'Wrong submission penalty', value: wrongSubmissionPenalty },
    ];
    return (
      <div className="contest-edit-configs-table__config">
        <h4>TROC style config</h4>
        <FormTable rows={rows} keyClassName="contest-edit-configs-table__key" />
        <hr />
      </div>
    );
  };

  const renderIcpcStyleConfig = ({ languageRestriction, wrongSubmissionPenalty }) => {
    const rows = [
      {
        key: 'languageRestriction',
        title: 'Allowed languages',
        value: formatLanguageRestriction(languageRestriction),
      },
      { key: 'wrongSubmissionPenalty', title: 'Wrong submission penalty', value: wrongSubmissionPenalty },
    ];
    return (
      <div className="contest-edit-configs-table__config">
        <h4>ICPC style config</h4>
        <FormTable rows={rows} keyClassName="contest-edit-configs-table__key" />
        <hr />
      </div>
    );
  };

  const renderIoiStyleConfig = ({ languageRestriction, usingLastAffectingPenalty, usingMaxScorePerSubtask }) => {
    const rows = [
      {
        key: 'languageRestriction',
        title: 'Allowed languages',
        value: formatLanguageRestriction(languageRestriction),
      },
      {
        key: 'usingLastAffectingPenalty',
        title: 'Using last affecting penalty?',
        value: usingLastAffectingPenalty ? <SmallTick /> : <SmallCross />,
      },
      {
        key: 'usingMaxScorePerSubtask',
        title: 'Using max score per subtask?',
        value: usingMaxScorePerSubtask ? <SmallTick /> : <SmallCross />,
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

  const renderGcjStyleConfig = ({ languageRestriction, wrongSubmissionPenalty }) => {
    const rows = [
      {
        key: 'languageRestriction',
        title: 'Allowed languages',
        value: formatLanguageRestriction(languageRestriction),
      },
      { key: 'wrongSubmissionPenalty', title: 'Wrong submission penalty', value: wrongSubmissionPenalty },
    ];
    return (
      <div className="contest-edit-configs-table__config">
        <h4>GCJ style config</h4>
        <FormTable rows={rows} keyClassName="contest-edit-configs-table__key" />
        <hr />
      </div>
    );
  };

  const renderClarificationTimeLimitConfig = ({ clarificationDuration }) => {
    const rows = [
      {
        key: 'clarificationDuration',
        title: 'Clarification duration',
        value: (
          <>
            <FormattedDuration value={clarificationDuration} /> (since contest start time)
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

  const renderDivisionConfig = ({ division }) => {
    const rows = [
      {
        key: 'divisionDivision',
        title: 'Division',
        value: <>{division}</>,
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

  const renderEditorialConfig = ({ preface }, profilesMap) => {
    return (
      <div className="contest-edit-configs-table__config">
        <h4>Editorial config</h4>
        <label>Preface</label>
        <ContentCard>
          <HtmlText profilesMap={profilesMap}>{preface || ''}</HtmlText>
        </ContentCard>
        <hr />
      </div>
    );
  };

  const renderScoreboardConfig = ({ isIncognitoScoreboard }) => {
    const rows = [
      {
        key: 'isIncognitoScoreboard',
        title: 'Incognito scoreboard?',
        value: isIncognitoScoreboard ? <SmallTick /> : <SmallCross />,
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

  const renderFrozenScoreboardConfig = ({ scoreboardFreezeTime, isOfficialScoreboardAllowed }) => {
    const rows = [
      {
        key: 'scoreboardFreezeTime',
        title: 'Freeze time',
        value: (
          <>
            <FormattedDuration value={scoreboardFreezeTime} /> (before contest end time)
          </>
        ),
      },
      {
        key: 'isIncognitoScoreboard',
        title: 'Is now unfrozen?',
        value: isOfficialScoreboardAllowed ? <SmallTick /> : <SmallCross />,
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

  const renderMergedScoreboardConfig = ({ previousContestJid }) => {
    const rows = [
      {
        key: 'previousContestJid',
        title: 'Previous contest JID',
        value: previousContestJid,
      },
    ];
    return (
      <div className="contest-edit-configs-table__config">
        <h4>Merged scoreboard config</h4>
        <FormTable rows={rows} keyClassName="contest-edit-configs-table__key" />
        <hr />
      </div>
    );
  };

  const renderExternalScoreboardConfig = ({ receiverUrl, receiverSecret }) => {
    const rows = [
      {
        key: 'receiverUrl',
        title: 'Receiver URL',
        value: receiverUrl,
      },
      {
        key: 'receiverSecret',
        title: 'Receiver secret',
        value: receiverSecret,
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

  const renderVirtualConfig = ({ virtualDuration }) => {
    const rows = [
      {
        key: 'virtualDuration',
        title: 'Virtual contest duration',
        value: <FormattedDuration value={virtualDuration} />,
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

  const formatLanguageRestriction = ({ allowedLanguageNames }) => {
    if (!allowedLanguageNames || allowedLanguageNames.length === 0) {
      return '(all)';
    }
    return allowedLanguageNames.sort().map(getGradingLanguageName).join(', ');
  };

  return (
    <div className="contest-edit-dialog__content">
      {config.trocStyle && renderTrocStyleConfig(config.trocStyle)}
      {config.icpcStyle && renderIcpcStyleConfig(config.icpcStyle)}
      {config.ioiStyle && renderIoiStyleConfig(config.ioiStyle)}
      {config.gcjStyle && renderGcjStyleConfig(config.gcjStyle)}
      {config.clarificationTimeLimit && renderClarificationTimeLimitConfig(config.clarificationTimeLimit)}
      {config.division && renderDivisionConfig(config.division)}
      {config.scoreboard && renderScoreboardConfig(config.scoreboard)}
      {config.frozenScoreboard && renderFrozenScoreboardConfig(config.frozenScoreboard)}
      {config.mergedScoreboard && renderMergedScoreboardConfig(config.mergedScoreboard)}
      {config.externalScoreboard && renderExternalScoreboardConfig(config.externalScoreboard)}
      {config.virtual && renderVirtualConfig(config.virtual)}
      {config.editorial && renderEditorialConfig(config.editorial, config.profilesMap)}
    </div>
  );
}
