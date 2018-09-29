import { configure } from 'enzyme';
import * as Adapter from 'enzyme-adapter-react-16';

configure({ adapter: new Adapter() });

(window as any).conf = {
  name: 'Judgels',
};

// https://github.com/yahoo/react-intl/issues/465#issuecomment-369566628
const consoleError = console.error.bind(console);
console.error = (message, ...args) => {
  if (typeof message === 'string' && message.startsWith('[React Intl] Missing message:')) {
    return;
  }
  consoleError(message, ...args);
};
