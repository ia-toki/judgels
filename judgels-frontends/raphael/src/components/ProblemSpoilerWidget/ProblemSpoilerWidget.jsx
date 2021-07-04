import { Switch, Alignment } from '@blueprintjs/core';
import { connect } from 'react-redux';

import { selectShowProblemDifficulty } from '../../modules/webPrefs/webPrefsSelectors';
import * as webPrefsActions from '../../modules/webPrefs/webPrefsActions';

function ProblemSpoilerWidget({ showProblemDifficulty, onChangeShowProblemDifficulty }) {
  const changeShowProblemDifficulty = ({ target }) => {
    onChangeShowProblemDifficulty(target.checked);
  };

  return (
    <Switch
      alignIndicator={Alignment.LEFT}
      label="Show difficulty"
      checked={showProblemDifficulty}
      onChange={changeShowProblemDifficulty}
    />
  );
}

const mapStateToProps = state => ({
  showProblemDifficulty: selectShowProblemDifficulty(state),
});

const mapDispatchToProps = {
  onChangeShowProblemDifficulty: webPrefsActions.switchShowProblemDifficulty,
};

export default connect(mapStateToProps, mapDispatchToProps)(ProblemSpoilerWidget);
