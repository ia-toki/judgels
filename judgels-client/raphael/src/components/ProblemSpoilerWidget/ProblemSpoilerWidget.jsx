import { Switch, Alignment } from '@blueprintjs/core';
import { connect } from 'react-redux';

import { selectShowProblemDifficulty, selectShowProblemTopicTags } from '../../modules/webPrefs/webPrefsSelectors';
import * as webPrefsActions from '../../modules/webPrefs/webPrefsActions';

import './ProblemSpoilerWidget.scss';

function ProblemSpoilerWidget({
  showProblemDifficulty,
  showProblemTopicTags,
  onChangeShowProblemDifficulty,
  onChangeShowProblemTopicTags,
}) {
  const changeShowProblemDifficulty = ({ target }) => {
    onChangeShowProblemDifficulty(target.checked);
  };
  const changeShowProblemTopicTags = ({ target }) => {
    onChangeShowProblemTopicTags(target.checked);
  };

  return (
    <>
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
    </>
  );
}

const mapStateToProps = state => ({
  showProblemDifficulty: selectShowProblemDifficulty(state),
  showProblemTopicTags: selectShowProblemTopicTags(state),
});

const mapDispatchToProps = {
  onChangeShowProblemDifficulty: webPrefsActions.switchShowProblemDifficulty,
  onChangeShowProblemTopicTags: webPrefsActions.switchShowProblemTopicTags,
};

export default connect(mapStateToProps, mapDispatchToProps)(ProblemSpoilerWidget);
