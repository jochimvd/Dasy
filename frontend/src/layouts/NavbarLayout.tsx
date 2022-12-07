import { Outlet } from "@solidjs/router";
import { Component } from "solid-js";
import Footer from "../components/Footer";
import NavBar from "../components/NavBar";

const NavbarLayout: Component = () => (
  <>
    <NavBar />
    <main class="max-w-7xl mx-auto pt-6 sm:pt-9 sm:px-6 lg:pt-12 lg:px-8">
      <Outlet />
    </main>
    <Footer />
  </>
);

export default NavbarLayout;
