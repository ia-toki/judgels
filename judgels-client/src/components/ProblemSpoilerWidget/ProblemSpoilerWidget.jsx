import { Alignment, Switch } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';

import { useWebPrefs } from '../../modules/webPrefs';

import './ProblemSpoilerWidget.scss';

export default function ProblemSpoilerWidget() {
  const { hideProblemDifficulty, showProblemTopicTags, setShowProblemDifficulty, setShowProblemTopicTags } =
    useWebPrefs();
  const showProblemDifficulty = !hideProblemDifficulty;

  const changeShowProblemDifficulty = ({ target }) => {
    setShowProblemDifficulty(target.checked);
  };
  const changeShowProblemTopicTags = ({ target }) => {
    setShowProblemTopicTags(target.checked);
  };

  return (
    <Flex gap={5}>
      <Switch
        className="problem-spoiler-switch"
        alignIndicator={Alignment.LEFT}
        label="Show difficulty"
        checked={showProblemDifficulty}
        onChange={changeShowProblemDifficulty}
      />
      <Switch
        className="problem-spoiler-switch"
        alignIndicator={Alignment.LEFT}
        label="Show tags"
        checked={showProblemTopicTags}
        onChange={changeShowProblemTopicTags}
      />
    </Flex>
  );
}
