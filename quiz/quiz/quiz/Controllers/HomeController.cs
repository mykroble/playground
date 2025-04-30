using System.Diagnostics;
using Microsoft.AspNetCore.Mvc;
using quiz.Models;

namespace quiz.Controllers;

public class HomeController : Controller
{
    private readonly ILogger<HomeController> _logger;

    public HomeController(ILogger<HomeController> logger)
    {
        _logger = logger;
    }

    public IActionResult Index()
    {
        return View();
    }

    public IActionResult Privacy()
    {
        return View();
    }
    public IActionResult makequiz()
    {
        return View();
    }
    public IActionResult makequiz2()
    {
        return View();
    }
    public IActionResult makequiz3()
    {
        return View();
    }
    public IActionResult admin()
    {
        return View();
    }
    public IActionResult studentview()
    {
        return View();
    }
    public IActionResult studentview2()
    {
        return View();
    }
    public IActionResult quiz()
    {
        return View();
    }
    public IActionResult quiz2()
    {
        return View();
    }
    public IActionResult quiz3()
    {
        return View();
    }
    [ResponseCache(Duration = 0, Location = ResponseCacheLocation.None, NoStore = true)]
    public IActionResult Error()
    {
        return View(new ErrorViewModel { RequestId = Activity.Current?.Id ?? HttpContext.TraceIdentifier });
    }
}

