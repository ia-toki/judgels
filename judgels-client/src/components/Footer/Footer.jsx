import { Flex } from '@blueprintjs/labs';
import HTMLReactParser from 'html-react-parser';

import { APP_CONFIG } from '../../conf';

import './Footer.scss';

export function Footer() {
  const footer = APP_CONFIG.footer || '© Ikatan Alumni TOKI';

  return (
    <div className="footer">
      <hr />
      <small className="footer__text">
        <Flex justifyContent="space-between">
          <div>{HTMLReactParser(footer)}</div>
          <div>
            Powered by <a href="https://github.com/ia-toki/judgels">Judgels</a>
          </div>
        </Flex>
      </small>
    </div>
  );
}
