import { SmallCross, SmallTick } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { Field } from 'react-final-form';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { FormattedDuration } from '../../../../../../components/FormattedDuration/FormattedDuration';
import { HtmlText } from '../../../../../../components/HtmlText/HtmlText';
import { FormCheckbox } from '../../../../../../components/forms/FormCheckbox/FormCheckbox';
import { FormRichTextArea } from '../../../../../../components/forms/FormRichTextArea/FormRichTextArea';
import { FormTableCheckbox } from '../../../../../../components/forms/FormTableCheckbox/FormTableCheckbox';
import { FormTableInput } from '../../../../../../components/forms/FormTableInput/FormTableInput';
import { FormTableTextInput } from '../../../../../../components/forms/FormTableTextInput/FormTableTextInput';
import { NonnegativeNumber, Required, composeValidators } from '../../../../../../components/forms/validations';
import { ContestStyle } from '../../../../../../modules/api/contest';
import { ContestModuleType } from '../../../../../../modules/api/contestModule';
import {
  allLanguagesAllowed,
  getGradingLanguageName,
  gradingLanguages,
} from '../../../../../../modules/api/gradingLanguage';
import { formatDuration, parseDuration } from '../../../../../../utils/duration';

// How each module's config is shown, turned into form values, and turned back into the config to save.
// A config spec either has `rows` and `fields` (a key-value table) or `renderView` and `renderFields` (anything else).
// All enabled modules are edited in one form, so each field is named through `name`, which puts it under the spec's key.

const tick = value => (value ? <SmallTick /> : <SmallCross />);

const formatLanguageRestriction = ({ allowedLanguageNames }) => {
  if (!allowedLanguageNames || allowedLanguageNames.length === 0) {
    return '(all)';
  }
  return [...allowedLanguageNames].sort().map(getGradingLanguageName).join(', ');
};

const fromLanguageRestriction = r => Object.assign({}, ...r.allowedLanguageNames.map(l => ({ [l]: true })));

const toLanguageRestriction = (allowAllLanguages, allowedLanguages) => ({
  allowedLanguageNames: allowAllLanguages ? [] : Object.keys(allowedLanguages || {}).filter(l => allowedLanguages[l]),
});

const renderAllowedLanguagesField = ({ allowAllLanguages }, name) => (
  <FormTableInput key="allowedLanguages" label="Allowed languages" meta={{}}>
    <Field component={FormCheckbox} name={name('allowAllLanguages')} label="(all)" />
    {!allowAllLanguages && (
      <Flex flexDirection="column" gap={2}>
        {gradingLanguages.map(lang => (
          <Field
            key={lang}
            component={FormCheckbox}
            name={name(`allowedLanguages.${lang}`)}
            label={getGradingLanguageName(lang)}
            small
          />
        ))}
      </Flex>
    )}
  </FormTableInput>
);

const wrongSubmissionPenaltyStyleConfig = key => ({
  key,
  rows: c => [
    { key: 'languageRestriction', title: 'Allowed languages', value: formatLanguageRestriction(c.languageRestriction) },
    { key: 'wrongSubmissionPenalty', title: 'Wrong submission penalty', value: c.wrongSubmissionPenalty },
  ],
  toFormValues: c => ({
    allowAllLanguages: allLanguagesAllowed(c.languageRestriction),
    allowedLanguages: fromLanguageRestriction(c.languageRestriction),
    wrongSubmissionPenalty: '' + c.wrongSubmissionPenalty,
  }),
  fields: (values, name) => [
    renderAllowedLanguagesField(values, name),
    <Field
      key="wrongSubmissionPenalty"
      component={FormTableTextInput}
      name={name('wrongSubmissionPenalty')}
      label="Wrong submission penalty"
      validate={composeValidators(Required, NonnegativeNumber)}
    />,
  ],
  toConfig: v => ({
    languageRestriction: toLanguageRestriction(v.allowAllLanguages, v.allowedLanguages),
    wrongSubmissionPenalty: +v.wrongSubmissionPenalty,
  }),
});

export const styleConfigs = {
  [ContestStyle.TROC]: wrongSubmissionPenaltyStyleConfig('trocStyle'),
  [ContestStyle.ICPC]: wrongSubmissionPenaltyStyleConfig('icpcStyle'),
  [ContestStyle.GCJ]: wrongSubmissionPenaltyStyleConfig('gcjStyle'),
  [ContestStyle.IOI]: {
    key: 'ioiStyle',
    rows: c => [
      {
        key: 'languageRestriction',
        title: 'Allowed languages',
        value: formatLanguageRestriction(c.languageRestriction),
      },
      {
        key: 'usingLastAffectingPenalty',
        title: 'Using last affecting penalty?',
        value: tick(c.usingLastAffectingPenalty),
      },
      { key: 'usingMaxScorePerSubtask', title: 'Using max score per subtask?', value: tick(c.usingMaxScorePerSubtask) },
    ],
    toFormValues: c => ({
      allowAllLanguages: allLanguagesAllowed(c.languageRestriction),
      allowedLanguages: fromLanguageRestriction(c.languageRestriction),
      usingLastAffectingPenalty: c.usingLastAffectingPenalty,
      usingMaxScorePerSubtask: c.usingMaxScorePerSubtask,
    }),
    fields: (values, name) => [
      renderAllowedLanguagesField(values, name),
      <Field
        key="usingLastAffectingPenalty"
        component={FormTableCheckbox}
        name={name('usingLastAffectingPenalty')}
        label="Using last affecting penalty?"
      />,
      <Field
        key="usingMaxScorePerSubtask"
        component={FormTableCheckbox}
        name={name('usingMaxScorePerSubtask')}
        label="Using max score per subtask?"
      />,
    ],
    toConfig: v => ({
      languageRestriction: toLanguageRestriction(v.allowAllLanguages, v.allowedLanguages),
      usingLastAffectingPenalty: !!v.usingLastAffectingPenalty,
      usingMaxScorePerSubtask: !!v.usingMaxScorePerSubtask,
    }),
  },
};

export const scoreboardConfig = {
  key: 'scoreboard',
  rows: c => [{ key: 'isIncognitoScoreboard', title: 'Incognito scoreboard?', value: tick(c.isIncognitoScoreboard) }],
  toFormValues: c => ({ isIncognitoScoreboard: c.isIncognitoScoreboard }),
  fields: (values, name) => [
    <Field
      key="isIncognitoScoreboard"
      component={FormTableCheckbox}
      name={name('isIncognitoScoreboard')}
      label="Incognito scoreboard?"
    />,
  ],
  toConfig: v => ({ isIncognitoScoreboard: !!v.isIncognitoScoreboard }),
};

export const moduleConfigs = {
  [ContestModuleType.ClarificationTimeLimit]: {
    key: 'clarificationTimeLimit',
    rows: c => [
      {
        key: 'clarificationDuration',
        title: 'Clarification duration',
        value: (
          <>
            <FormattedDuration value={c.clarificationDuration} /> (since contest start time)
          </>
        ),
      },
    ],
    toFormValues: c => ({ clarificationDuration: formatDuration(c.clarificationDuration) }),
    fields: (values, name) => [
      <Field
        key="clarificationDuration"
        component={FormTableTextInput}
        name={name('clarificationDuration')}
        label="Clarification duration"
        inputHelper="Since contest start time. Example: 2h 30m"
        validate={Required}
      />,
    ],
    toConfig: v => ({ clarificationDuration: parseDuration(v.clarificationDuration) }),
  },

  [ContestModuleType.Division]: {
    key: 'division',
    rows: c => [
      { key: 'division', title: 'Division', value: c.division },
      {
        key: 'allowHigherDivisionsUnofficially',
        title: 'Allow higher divisions to participate unofficially?',
        value: tick(c.allowHigherDivisionsUnofficially),
      },
    ],
    toFormValues: c => ({
      division: '' + c.division,
      allowHigherDivisionsUnofficially: c.allowHigherDivisionsUnofficially,
    }),
    fields: (values, name) => [
      <Field
        key="division"
        component={FormTableTextInput}
        name={name('division')}
        label="Division"
        validate={Required}
      />,
      <Field
        key="allowHigherDivisionsUnofficially"
        component={FormTableCheckbox}
        name={name('allowHigherDivisionsUnofficially')}
        label="Allow higher divisions to participate unofficially?"
      />,
    ],
    toConfig: v => ({
      division: +v.division,
      allowHigherDivisionsUnofficially: !!v.allowHigherDivisionsUnofficially,
    }),
  },

  [ContestModuleType.Editorial]: {
    key: 'editorial',
    renderView: (c, profilesMap) => (
      <Flex flexDirection="column" gap={2}>
        <label>Preface</label>
        <ContentCard compact>
          <HtmlText profilesMap={profilesMap}>{c.preface || ''}</HtmlText>
        </ContentCard>
      </Flex>
    ),
    toFormValues: c => ({ preface: c.preface }),
    renderFields: (values, name) => (
      <Field component={FormRichTextArea} name={name('preface')} label="Preface" rows={15} />
    ),
    toConfig: v => ({ preface: v.preface }),
  },

  [ContestModuleType.FrozenScoreboard]: {
    key: 'frozenScoreboard',
    rows: c => [
      {
        key: 'scoreboardFreezeTime',
        title: 'Freeze time',
        value: (
          <>
            <FormattedDuration value={c.scoreboardFreezeTime} /> (before contest end time)
          </>
        ),
      },
      { key: 'isOfficialScoreboardAllowed', title: 'Is now unfrozen?', value: tick(c.isOfficialScoreboardAllowed) },
    ],
    toFormValues: c => ({
      scoreboardFreezeTime: formatDuration(c.scoreboardFreezeTime),
      isOfficialScoreboardAllowed: c.isOfficialScoreboardAllowed,
    }),
    fields: (values, name) => [
      <Field
        key="scoreboardFreezeTime"
        component={FormTableTextInput}
        name={name('scoreboardFreezeTime')}
        label="Freeze time"
        inputHelper="Before contest end time. Example: 2h 30m"
        validate={Required}
      />,
      <Field
        key="isOfficialScoreboardAllowed"
        component={FormTableCheckbox}
        name={name('isOfficialScoreboardAllowed')}
        label="Is now unfrozen?"
      />,
    ],
    toConfig: v => ({
      scoreboardFreezeTime: parseDuration(v.scoreboardFreezeTime),
      isOfficialScoreboardAllowed: !!v.isOfficialScoreboardAllowed,
    }),
  },

  [ContestModuleType.MergedScoreboard]: {
    key: 'mergedScoreboard',
    rows: c => [{ key: 'previousContestJid', title: 'Previous contest JID', value: c.previousContestJid }],
    toFormValues: c => ({ previousContestJid: c.previousContestJid }),
    fields: (values, name) => [
      <Field
        key="previousContestJid"
        component={FormTableTextInput}
        name={name('previousContestJid')}
        label="Previous contest JID"
        validate={Required}
      />,
    ],
    toConfig: v => ({ previousContestJid: v.previousContestJid }),
  },

  [ContestModuleType.ExternalScoreboard]: {
    key: 'externalScoreboard',
    rows: c => [
      { key: 'receiverUrl', title: 'Receiver URL', value: c.receiverUrl },
      { key: 'receiverSecret', title: 'Receiver secret', value: c.receiverSecret },
    ],
    toFormValues: c => ({ receiverUrl: c.receiverUrl, receiverSecret: c.receiverSecret }),
    fields: (values, name) => [
      <Field
        key="receiverUrl"
        component={FormTableTextInput}
        name={name('receiverUrl')}
        label="Receiver URL"
        validate={Required}
      />,
      <Field
        key="receiverSecret"
        component={FormTableTextInput}
        name={name('receiverSecret')}
        label="Receiver secret"
      />,
    ],
    toConfig: v => ({ receiverUrl: v.receiverUrl, receiverSecret: v.receiverSecret }),
  },

  [ContestModuleType.Virtual]: {
    key: 'virtual',
    rows: c => [
      {
        key: 'virtualDuration',
        title: 'Virtual contest duration',
        value: <FormattedDuration value={c.virtualDuration} />,
      },
    ],
    toFormValues: c => ({ virtualDuration: formatDuration(c.virtualDuration) }),
    fields: (values, name) => [
      <Field
        key="virtualDuration"
        component={FormTableTextInput}
        name={name('virtualDuration')}
        label="Virtual contest duration"
        inputHelper="Example: 2h 30m"
        validate={Required}
      />,
    ],
    toConfig: v => ({ virtualDuration: parseDuration(v.virtualDuration) }),
  },
};
