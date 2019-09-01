import { configure } from 'enzyme';
import Adapter from 'enzyme-adapter-react-16';

configure({ adapter: new Adapter() });

(window as any).conf = {
  name: 'Judgels',
  slogan: 'Judgment Angels',
  apiUrls: {
    jophiel: 'http://localhost:9001/api/v2',
    legacyJophiel: 'http://localhost:9001/api/legacy',
    uriel: 'http://localhost:9004/api/v2',
  },
  tempHome: {
    jerahmeelUrl: 'http://jerahmeel',
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
