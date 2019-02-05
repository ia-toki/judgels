import { configure } from 'enzyme';
import * as Adapter from 'enzyme-adapter-react-16';

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
  termsAndConditions: {
    contest: `<p>By competing in this contests, you agree that:</p>
              <ul>
                <li> You will not collaborate with any other contestants. </li>
                <li> You will not use fake or multiple accounts, other than your own account. </li>
                <li> You will not try to hack or attack the contest system in any way. </li>
              </ul>
              <p> Failure to comply with the above rules can result to a disqualification or ban. </p>
              <p>Enjoy the contest!</p>`,
  },
};

(window as any).scrollTo = function() {
  return;
};

// https://github.com/yahoo/react-intl/issues/465#issuecomment-369566628
const consoleError = console.error.bind(console);
console.error = (message, ...args) => {
  if (typeof message === 'string' && message.startsWith('[React Intl] Missing message:')) {
    return;
  }
  consoleError(message, ...args);
};
