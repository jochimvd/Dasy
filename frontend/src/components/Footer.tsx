const Footer = () => {
  return (
    <footer class="max-w-7xl mx-auto px-6 lg:px-8 my-8 text-right">
      <span class="mr-2 text-sm text-gray-300 hover:text-gray-400 transition-colors">
        &copy; {new Date().getFullYear()} DASY. All rights reserved.
      </span>
    </footer>
  );
};

export default Footer;
