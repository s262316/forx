package com.github.s262316.forx.core.real;

import java.util.List;
import java.util.UUID;

import org.dyn4j.collision.Collidable;
import org.dyn4j.collision.Fixture;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;

public class Onscreen implements Collidable
{
	private AABB a1;
	
	public Onscreen(double x1, double y1, double width, double height)
	{
		a1=new AABB(x1, y1, x1+width-1, y1+height-1);
	}
	
	@Override
	public void rotate(double theta)
	{
		throw new RuntimeException("not implemented");
	}

	@Override
	public void rotate(double theta, Vector2 point)
	{
		throw new RuntimeException("not implemented");
	}

	@Override
	public void rotate(double theta, double x, double y)
	{
		throw new RuntimeException("not implemented");
	}

	@Override
	public void translate(double x, double y)
	{
		throw new RuntimeException("not implemented");
	}

	@Override
	public void translate(Vector2 vector)
	{
		throw new RuntimeException("not implemented");
	}

	@Override
	public UUID getId()
	{
		return UUID.randomUUID();
	}

	@Override
	public AABB createAABB()
	{
		return a1;
	}

	@Override
	public Fixture getFixture(int index)
	{
		throw new RuntimeException("not implemented");
	}

	@Override
	public int getFixtureCount()
	{
		throw new RuntimeException("not implemented");
	}

	@Override
	public List<? extends Fixture> getFixtures()
	{
		throw new RuntimeException("not implemented");
	}

	@Override
	public Transform getTransform()
	{
		throw new RuntimeException("not implemented");
	}
}
