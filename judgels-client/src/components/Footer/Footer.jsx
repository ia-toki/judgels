import HTMLReactParser from 'html-react-parser';

import { APP_CONFIG } from '../../conf';

import './Footer.scss';

export function Footer() {
  const footer = APP_CONFIG.footer || '© Ikatan Alumni TOKI';

  return (
    <div className="footer">
      <hr />
      <small className="footer__text">
        <div className="float-left">{HTMLReactParser(footer)}</div>
        <div className="float-right">
          Powered by <a href="https://github.com/ia-toki/judgels">Judgels</a>
        </div>
        <div className="clearfix" />
      </small>
    </div>
  );
}
