import { configure } from 'enzyme';
import Adapter from 'enzyme-adapter-react-16';
import nock from 'nock';

configure({ adapter: new Adapter() });

nock.disableNetConnect();

(window as any).conf = {
  name: 'Judgels',
  slogan: 'Judgment Angels',
  apiUrls: {
    jophiel: 'http://jophiel',
    legacyJophiel: 'http://jophiel-legacy',
    uriel: 'http://uriel',
    jerahmeel: 'http://jerahmeel',
  },
  welcomeBanner: {
    title: 'Welcome to Judgels',
    description:
      'Lorem ipsum dolor sit amet, consectetur adipisicing elit.Lorem ipsum dolor sit amet, consectetur adipisicing elit. Lorem ipsum dolor sit amet, consectetur adipisicing elit. Lorem ipsum dolor sit amet, consectetur adipisicing elit.',
  },
};

(window as any).scrollTo = function() {
  return;
};

// https://github.com/yahoo/react-intl/issues/465#issuecomment-369566628
const consoleError = console.error.bind(console);
console.error = (message, ...args) => {
  if (typeof message === 'string' && message.startsWith('[React Intl] Error formatting relative time')) {
    return;
  }
  consoleError(message, ...args);
};
