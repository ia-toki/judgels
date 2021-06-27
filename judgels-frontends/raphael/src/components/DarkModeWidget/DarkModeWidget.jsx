import { Navbar, Switch, Alignment, Tag, Intent } from '@blueprintjs/core';
import { connect } from 'react-redux';

import { selectIsDarkMode, selectIsNewToDarkMode } from '../../modules/webPrefs/webPrefsSelectors';
import * as webPrefsActions from '../../modules/webPrefs/webPrefsActions';

import './DarkModeWidget.scss';

function DarkModeWidget({ isDarkMode, isNewToDarkMode, onChangeDarkMode }) {
  const changeDarkMode = ({ target }) => {
    onChangeDarkMode(target.checked);
  };

  const onboarding = isNewToDarkMode && (
    <Tag className="dark-mode-widget__onboarding" intent={Intent.WARNING}>
      NEW!
    </Tag>
  );
  const widget = (
    <Switch
      className="dark-mode-widget__switch"
      alignIndicator={Alignment.RIGHT}
      label="dark mode"
      checked={isDarkMode}
      onChange={changeDarkMode}
    />
  );

  return (
    <>
      <Navbar.Group align={Alignment.RIGHT}>{widget}</Navbar.Group>
      <Navbar.Group align={Alignment.RIGHT}>{onboarding}</Navbar.Group>
    </>
  );
}

const mapStateToProps = state => ({
  isNewToDarkMode: selectIsNewToDarkMode(state),
  isDarkMode: selectIsDarkMode(state),
});

const mapDispatchToProps = {
  onChangeDarkMode: webPrefsActions.switchDarkMode,
};

export default connect(mapStateToProps, mapDispatchToProps)(DarkModeWidget);
