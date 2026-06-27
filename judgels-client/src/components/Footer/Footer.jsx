import { Flex } from '@blueprintjs/labs';

import './Footer.scss';

export function Footer() {
  return (
    <div className="footer">
      <hr />
      <small className="footer__text">
        <Flex justifyContent="end">
          <div>
            Powered by <a href="https://github.com/ia-toki/judgels">Judgels</a>
          </div>
        </Flex>
      </small>
    </div>
  );
}
