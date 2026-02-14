import { Alignment, Navbar, Switch } from '@blueprintjs/core';
import { Moon } from '@blueprintjs/icons';

import { useWebPrefs } from '../../modules/webPrefs';

import './DarkModeWidget.scss';

export default function DarkModeWidget() {
  const { isDarkMode, setIsDarkMode } = useWebPrefs();

  const changeDarkMode = ({ target }) => {
    setIsDarkMode(target.checked);
  };

  const widget = (
    <Switch
      className="dark-mode-widget__switch"
      alignIndicator={Alignment.RIGHT}
      label={<Moon />}
      checked={isDarkMode}
      onChange={changeDarkMode}
    />
  );

  return <Navbar.Group align={Alignment.RIGHT}>{widget}</Navbar.Group>;
}
